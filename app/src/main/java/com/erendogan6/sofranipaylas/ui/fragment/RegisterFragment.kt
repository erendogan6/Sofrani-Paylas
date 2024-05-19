package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.sofranipaylas.R
import com.erendogan6.sofranipaylas.databinding.FragmentRegisterBinding
import com.erendogan6.sofranipaylas.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val surname = binding.surnameEditText.text.toString().trim()
            val email = binding.emailRegisterEditText.text.toString().trim()
            val password = binding.passwordRegisterEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val userName = binding.userNameEditText.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || userName.isEmpty()) {
                Toast.makeText(activity, "All fields are required.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(email, password, name, surname, phone, userName)
            }
        }

        viewModel.userRegistrationResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                Toast.makeText(activity, "Registration successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Registration failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
