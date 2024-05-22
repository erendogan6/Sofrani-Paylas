package com.erendogan6.sofranipaylas.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Post(
    @PropertyName("date")
    val date: Timestamp = Timestamp.now(),

    @PropertyName("description")
    val description: String = "",

    @PropertyName("eventStatus")
    val eventStatus: Boolean = true,

    @PropertyName("hostID")
    val hostID: String = "",

    @PropertyName("image")
    val image: String = "",

    val latitude: Double = 0.0,

    val longitude: Double = 0.0,

    @PropertyName("maxParticipants")
    val maxParticipants: Int = 0,

    @PropertyName("participants")
    val participants: List<String>? = listOf(),

    @PropertyName("relatedFoods")
    val relatedFoods: List<String> = listOf(),

    @PropertyName("title")
    val title: String = "",

    var hostUserName: String = "")
