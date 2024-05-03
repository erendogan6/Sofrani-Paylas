package com.erendogan6.sofranipaylas.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.sofranipaylas.repository.Repository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> = _uploadStatus

    private val _submitStatus = MutableLiveData<String>()
    val submitStatus: LiveData<String> = _submitStatus

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            repository.uploadImageAndGetUrl(imageUri).collect { result ->
                result.onSuccess { url ->
                    _imageUrl.value = url
                    _uploadStatus.value = "Upload successful"
                }.onFailure { throwable ->
                    _uploadStatus.value = "Image upload failed: ${throwable.message}"
                }
            }
        }
    }

    fun submitPost(title: String, description: String, participants: Int, imageUrl: String, date: Timestamp) {
        viewModelScope.launch {
            repository.submitPost(title, description, participants, imageUrl, date).collect { result ->
                result.onSuccess {
                    _submitStatus.value = "Post submitted successfully"
                }.onFailure { throwable ->
                    _submitStatus.value = "Failed to submit post: ${throwable.message}"
                }
            }
        }
    }


}