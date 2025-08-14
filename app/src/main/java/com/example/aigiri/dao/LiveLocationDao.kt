package com.example.aigiri.dao


import com.example.aigiri.network.FirebaseConfig
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LiveLocationDao {

    private val db: DatabaseReference =
        FirebaseDatabase.getInstance(FirebaseConfig.databaseURL).reference

    fun updateUserLocation(
        userId: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val path = db.child("users").child(userId).child("location")
        val data = mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to timestamp
        )

        path.setValue(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
