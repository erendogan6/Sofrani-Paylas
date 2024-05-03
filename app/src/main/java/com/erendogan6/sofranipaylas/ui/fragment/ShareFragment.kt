package com.erendogan6.sofranipaylas.ui.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
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
import androidx.fragment.app.viewModels
import com.erendogan6.sofranipaylas.databinding.FragmentShareBinding
import com.erendogan6.sofranipaylas.viewmodel.ShareViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareFragment : Fragment() {
    private var selectedImageUri: Uri? = null
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShareViewModel by viewModels()
    private var selectedDate: Timestamp? = null


    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.imagePickerButton.setOnClickListener {
            loadOrRequestPermission()
        }

        binding.submitButton.setOnClickListener {
            submitPost()
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
            val pickedDate = Calendar.getInstance()
            pickedDate.set(year, month, dayOfMonth)
            selectedDate = Timestamp(pickedDate.time) // Use java.util.Date from Calendar
            toastGoster("Selected Date: ${dayOfMonth}/${month + 1}/$year")
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun submitPost() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val participants = binding.participantsEditText.text.toString().toIntOrNull() ?: 0
        val imageUri = selectedImageUri
        val date = selectedDate ?: Timestamp.now()

        if (title.isEmpty() || description.isEmpty() || imageUri == null) {
            toastGoster("Başlık, açıklama ve bir resim eklemelisiniz.")
            return
        }

        viewModel.uploadImage(imageUri)
        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                viewModel.submitPost(title, description, participants, imageUrl, date)
            } else {
                toastGoster("Resim yüklenemedi, tekrar deneyiniz.")
            }
        }
        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
            toastGoster(status)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}