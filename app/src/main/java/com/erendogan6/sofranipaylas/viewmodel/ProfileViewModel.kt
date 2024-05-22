package com.erendogan6.sofranipaylas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.sofranipaylas.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _changePasswordResult = MutableLiveData<Boolean>()
    val changePasswordResult: LiveData<Boolean> get() = _changePasswordResult

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            repository.changePassword(currentPassword, newPassword).collect { result ->
                _changePasswordResult.value = result
            }
        }
    }
}