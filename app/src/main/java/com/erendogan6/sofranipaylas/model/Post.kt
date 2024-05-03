package com.erendogan6.sofranipaylas.model

import com.google.firebase.Timestamp

data class Post(val date: Timestamp, val description: String, val images: String, val participants: Int, val status: Boolean, val title: String, val userID: String)
