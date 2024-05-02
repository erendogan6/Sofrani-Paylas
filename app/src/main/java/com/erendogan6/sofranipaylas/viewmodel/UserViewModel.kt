package com.erendogan6.sofranipaylas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.erendogan6.sofranipaylas.model.User
import com.erendogan6.sofranipaylas.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _userLoginResult = MutableLiveData<User?>()
    val userLoginResult: LiveData<User?> = _userLoginResult

    private val _userRegistrationResult = MutableLiveData<Boolean>()
    val userRegistrationResult: LiveData<Boolean> = _userRegistrationResult

    fun login(email: String, password: String) {
        val result = userRepository.loginUser(email, password)
        result.observeForever {
            _userLoginResult.value = it
        }
    }

    fun register(email: String, password: String, fullname: String) {
        userRepository.registerUser(email, password, fullname).observeForever {
            _userRegistrationResult.value = it
        }
    }
}
