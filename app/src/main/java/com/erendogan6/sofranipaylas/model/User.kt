package com.erendogan6.sofranipaylas.model

data class User(
    val about: String,
    val email: String,
    val fullname: String,
    val isHost: Boolean,
    val profilePicture: String,
    val userID: String
)
