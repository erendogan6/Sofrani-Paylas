package com.erendogan6.sofranipaylas.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.erendogan6.sofranipaylas.R
import com.erendogan6.sofranipaylas.databinding.FragmentShareBinding
import com.erendogan6.sofranipaylas.extensions.checkUserSessionAndNavigate
import com.erendogan6.sofranipaylas.viewmodel.ShareViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ShareFragment : Fragment() {
    private var selectedImageUri: Uri? = null
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShareViewModel by activityViewModels()
    private var selectedDate: Timestamp? = null


    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data
            viewModel.setSelectedImageUri(selectedImageUri)
            binding.shareImage.setImageURI(selectedImageUri)
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            toastGoster("Galeri erişimi için izin gerekli")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        checkUserSessionAndNavigate()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        binding.dateText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.shareImage.setOnClickListener {
            loadOrRequestPermission()
        }

        binding.submitButton.setOnClickListener {
            submitPost()
        }

        binding.locationPickerButton.setOnClickListener {
            findNavController().navigate(R.id.action_shareFragment_to_locationPickerFragment)
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageUri.observe(viewLifecycleOwner, Observer { uri ->
            binding.shareImage.setImageURI(uri)
        })

        viewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                binding.locationTextView.text = "Seçilen Konum: (${it.latitude}, ${it.longitude})"
                val address = getAddressFromLatLng(it)
                binding.locationTextView.text = address ?: "Adres bulunamadı"
            }
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses.let {
                if (addresses?.isNotEmpty()!!) {
                    val address = addresses[0]
                    "${address.getAddressLine(0)}"
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun loadOrRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            "android.permission.READ_MEDIA_IMAGES"
        } else {
            "android.permission.READ_EXTERNAL_STORAGE"
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            if (shouldShowRequestPermissionRationale(permission)) {
                Snackbar.make(binding.root, "Galeri için izin gereklidir", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver") {
                    permissionLauncher.launch(permission)
                }.show()
            } else {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }

    private fun toastGoster(mesaj: String) {
        Toast.makeText(requireActivity(), mesaj, Toast.LENGTH_SHORT).show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            showTimePickerDialog(year, month, dayOfMonth)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePickerDialog(year: Int, month: Int, dayOfMonth: Int) {
        val timeCalendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val formattedDateTime = formatter.format(pickedDateTime.time)
            binding.dateText.setText(formattedDateTime)
            selectedDate = Timestamp(pickedDateTime.time)
        }, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), true).show()
    }


    private fun submitPost() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val participants = binding.participantsEditText.text.toString().toIntOrNull() ?: 0
        val imageUri = selectedImageUri
        val date = selectedDate ?: Timestamp.now()
        val latitude = viewModel.selectedLocation.value?.latitude ?: 0.0
        val longitude = viewModel.selectedLocation.value?.longitude ?: 0.0

        if (title.isEmpty() || description.isEmpty() || imageUri == null) {
            toastGoster("Başlık, açıklama ve bir resim eklemelisiniz.")
            return
        }

        viewModel.uploadImage(imageUri)
        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                viewModel.submitPost(title, description, participants, imageUrl, date, latitude, longitude)
                viewModel.submitStatus.observe(viewLifecycleOwner) { status ->
                    if (status) {
                        toastGoster("Gönderi başarıyla oluşturuldu.")
                        findNavController().navigate(R.id.action_shareFragment_to_homeFragment)
                    } else {
                        toastGoster("Gönderi oluşturulamadı.")
                    }
                }
            } else {
                toastGoster("Resim yüklenemedi.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}