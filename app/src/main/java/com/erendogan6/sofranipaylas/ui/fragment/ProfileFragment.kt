package com.erendogan6.sofranipaylas.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.erendogan6.sofranipaylas.databinding.FragmentProfileBinding
import com.erendogan6.sofranipaylas.extensions.checkUserSessionAndNavigate
import com.erendogan6.sofranipaylas.model.User
import com.erendogan6.sofranipaylas.ui.activity.LoginActivity
import com.erendogan6.sofranipaylas.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        checkUserSessionAndNavigate()
        setupImagePicker()
        setupClickListeners()
        observeViewModel()

        viewModel.getCurrentUser()

        return binding.root
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    viewModel.uploadProfileImage(it)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.cikisYapLayout.setOnClickListener {
            signOut()
        }

        binding.profilResimLayout.setOnClickListener {
            pickImage()
        }
    }

    private fun observeViewModel() {
        viewModel.uploadImageResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess { imageUrl ->
                    updateProfileImage(imageUrl)
                    showSnackbar("Profil resmi başarıyla yüklendi")
                }.onFailure { throwable ->
                    showSnackbar("Profil resmi yüklenirken hata oluştu: ${throwable.message}")
                }
                viewModel.resetUploadImageResult()
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                updateUserProfile(it)
            }
        }
    }

    private fun updateProfileImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).transform(CircleCrop()).into(binding.profilResim)
    }

    private fun updateUserProfile(user: User) {
        binding.profilMail.text = user.email
        binding.profileTextView.text = "Hoşgeldin ${user.userName}"
        if (user.profilePicture.isNotEmpty()) {
            updateProfileImage(user.profilePicture)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
