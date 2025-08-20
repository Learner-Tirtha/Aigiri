package com.example.aigiri.model

import com.google.android.gms.maps.model.LatLng
data class RouteInfo(
    val points: List<LatLng>,
    val distanceMeters: Double,
    val durationSeconds: Double
)
