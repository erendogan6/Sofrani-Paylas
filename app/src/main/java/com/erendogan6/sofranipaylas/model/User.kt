package com.erendogan6.sofranipaylas.model

import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("about")
    val about: String = "",
    @PropertyName("email")
    val email: String = "",
    @PropertyName("isHost")
    val isHost: Boolean = false,
    @PropertyName("name")
    val name: String = "",
    @PropertyName("phone")
    val phone: String? = null,
    @PropertyName("profilePicture")
    val profilePicture: String = "",
    @PropertyName("role")
    val role: String = "",
    @PropertyName("surname")
    val surname: String = "",
    @PropertyName("userName")
    val userName: String = "")
