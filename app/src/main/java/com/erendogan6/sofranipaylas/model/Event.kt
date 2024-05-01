package com.erendogan6.sofranipaylas.model

import com.google.firebase.Timestamp
data class Event(
    val date: Timestamp,
    val description: String,
    val eventID: String,
    val eventStatus: String,
    val hostID: String,
    val images: String,
    val location: Timestamp,
    val maxParticipants: Int,
    val participantIDs: List<String>,
    val time: String,
    val title: String
)
