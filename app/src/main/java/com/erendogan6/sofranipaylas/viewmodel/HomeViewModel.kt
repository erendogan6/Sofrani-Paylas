package com.erendogan6.sofranipaylas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.sofranipaylas.model.Post
import com.erendogan6.sofranipaylas.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _posts = MutableLiveData<List<Post?>>()
    val posts: LiveData<List<Post?>> = _posts

    init {
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch {
            repository.getPosts().collect() { postList ->
                _posts.value = postList
            }
        }
    }
}