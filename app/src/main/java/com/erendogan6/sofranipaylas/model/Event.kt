package com.erendogan6.sofranipaylas.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Event(val date: Timestamp?, val description: String, val eventID: String, val eventStatus: String, val hostID: String, val images: String, val location: GeoPoint?, val maxParticipants: Int, val participantIDs: List<String>, val title: String)
