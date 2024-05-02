package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.erendogan6.sofranipaylas.R
import com.erendogan6.sofranipaylas.databinding.FragmentRegisterBinding
import com.erendogan6.sofranipaylas.viewmodel.UserViewModel

class RegisterFragment : Fragment() {
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.registerButton.setOnClickListener {
            val email = binding.emailRegisterEditText.text.toString().trim()
            val password = binding.passwordRegisterEditText.text.toString().trim()
            val fullname = binding.fullnameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(activity, "All fields are required.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(email, password, fullname)
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

        return binding.root
    }

}