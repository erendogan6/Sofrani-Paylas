package com.erendogan6.sofranipaylas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.sofranipaylas.model.User
import com.erendogan6.sofranipaylas.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _userLoginResult = MutableLiveData<User?>()
    val userLoginResult: LiveData<User?> = _userLoginResult

    private val _userRegistrationResult = MutableLiveData<Boolean>()
    val userRegistrationResult: LiveData<Boolean> = _userRegistrationResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.loginUser(email, password).collect { user ->
                _userLoginResult.value = user
            }
        }
    }

    fun register(email: String, password: String, name: String, surname: String, phone: String, userName: String) {
        viewModelScope.launch {
            repository.registerUser(email, password, name, surname, phone, userName).collect { result ->
                _userRegistrationResult.value = result
            }
        }
    }
}
