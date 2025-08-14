package com.example.aigiri.repository

import android.util.Log
import com.example.aigiri.dao.LiveLocationDao

class LiveLocationRepository(private val liveLocationDao: LiveLocationDao) {

    fun updateUserLocation(userId: String, latitude: Double, longitude: Double, timestamp: Long) {
        liveLocationDao.updateUserLocation(
            userId = userId,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp,
            onSuccess = {
                Log.d("LiveLocationRepo", "✅ Location updated for $userId")
            },
            onFailure = { e ->
                Log.e("LiveLocationRepo", "❌ Failed to update location: ${e.message}", e)
            }
        )
    }
}
