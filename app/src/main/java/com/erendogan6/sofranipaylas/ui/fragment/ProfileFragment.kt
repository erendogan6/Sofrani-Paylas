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

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    viewModel.uploadProfileImage(imageUri)
                }
            }
        }

        binding.cikisYapLayout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.profilResimLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess { imageUrl ->
                    Glide.with(this).load(imageUrl).transform(CircleCrop()).into(binding.profilResim)
                    Snackbar.make(binding.root, "Profil resmi başarıyla yüklendi", Snackbar.LENGTH_SHORT).show()
                }.onFailure {
                    Snackbar.make(binding.root, "Profil resmi yüklenirken hata oluştu", Snackbar.LENGTH_SHORT).show()
                }
                viewModel.resetUploadImageResult()
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profilMail.text = it.email
                if (it.profilePicture.isNotEmpty()) {
                    Glide.with(this).load(it.profilePicture).transform(CircleCrop()).into(binding.profilResim)
                }
            }
        }

        viewModel.getCurrentUser()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
