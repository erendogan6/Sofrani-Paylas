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
class NearbyPostsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    fun fetchNearbyPosts(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = repository.getNearbyPosts(latitude, longitude)
            _posts.value = result
        }
    }
}
