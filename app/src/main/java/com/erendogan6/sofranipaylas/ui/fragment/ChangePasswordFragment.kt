package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.sofranipaylas.databinding.FragmentChangePasswordBinding
import com.erendogan6.sofranipaylas.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        binding.btnChangePassword.setOnClickListener {
            val currentPassword = binding.CurrentPasswordEditText.text.toString()
            val newPassword = binding.NewPasswordEditText.text.toString()
            val confirmPassword = binding.ConfirmPasswordEditText.text.toString()

            if (newPassword == confirmPassword) {
                viewModel.changePassword(currentPassword, newPassword)
            } else {
                Snackbar.make(binding.root, "Yeni şifreler eşleşmiyor", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.changePasswordResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                Snackbar.make(binding.root, "Şifre başarıyla değiştirildi", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Snackbar.make(binding.root, "Şifre değiştirme başarısız", Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
