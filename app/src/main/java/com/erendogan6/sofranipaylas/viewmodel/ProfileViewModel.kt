package com.erendogan6.sofranipaylas.viewmodel

import android.net.Uri
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
class ProfileViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _changePasswordResult = MutableLiveData<Boolean>()
    val changePasswordResult: LiveData<Boolean> get() = _changePasswordResult

    private val _uploadImageResult = MutableLiveData<Result<String>?>()
    val uploadImageResult: MutableLiveData<Result<String>?> get() = _uploadImageResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            repository.changePassword(currentPassword, newPassword).collect { result ->
                _changePasswordResult.value = result
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            repository.updateProfilePicture(imageUri).collect { result ->
                _uploadImageResult.value = result
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun resetUploadImageResult() {
        _uploadImageResult.value = null
    }
}
