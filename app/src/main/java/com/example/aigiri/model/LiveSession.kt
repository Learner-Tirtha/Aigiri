package com.example.aigiri.model


data class LiveSession(
    val userId: String = "",
    val roomId: String = "",
    val joinUrl: String = "",
    val token: String = "",
    val wsUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val recordingUrl: String? = null
)
