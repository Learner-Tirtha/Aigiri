package com.example.aigiri.model

import com.google.firebase.firestore.Exclude

data class EmergencyContact(
    var name: String = "",
    var phoneNumber: String = "",
    var priority: Int = 0   ,       // Used for sorting contacts
    @get:Exclude
    val id: String = ""
)
