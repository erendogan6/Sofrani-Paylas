package com.erendogan6.sofranipaylas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.sofranipaylas.model.Post
import com.erendogan6.sofranipaylas.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _joinStatus = MutableLiveData<Result<Boolean>>()
    val joinStatus: LiveData<Result<Boolean>> = _joinStatus

    private val _isAlreadyJoined = MutableLiveData<Boolean>()
    val isAlreadyJoined: LiveData<Boolean> = _isAlreadyJoined

    fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getPostById(postId)
                if (result != null) {
                    _post.value = result
                    checkIfAlreadyJoined(result)
                    Log.d("PostDetailViewModel", "Post loaded successfully: $result")
                } else {
                    Log.e("PostDetailViewModel", "Failed to load post with id: $postId")
                }
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Error loading post with id: $postId", e)
            }
        }
    }


    fun joinPost(postId: String) {
        viewModelScope.launch {
            val result = repository.joinPost(postId)
            _joinStatus.value = result
        }
    }

    private fun checkIfAlreadyJoined(post: Post) {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        _isAlreadyJoined.value = post.participants?.contains(currentUserID) == true
    }
}
