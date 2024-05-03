package com.erendogan6.sofranipaylas.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Post(
    @PropertyName("date")
    val date: Timestamp = Timestamp.now(),
    @PropertyName("description")
    val description: String = "",
    @PropertyName("images")
    val images: String = "",
    @PropertyName("participants")
    val participants: Int = 0,
    @PropertyName("status")
    val status: Boolean = false,
    @PropertyName("title")
    val title: String = "",
    @PropertyName("userID")
    val userID: String = "")
