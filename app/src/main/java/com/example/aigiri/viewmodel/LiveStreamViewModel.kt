package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aigiri.network.TokenManager
import com.example.aigiri.network.ZegoConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

data class LiveStreamSession(
    val userID: String,
    val userName: String,
    val liveID: String,
    val appID: Long,
    val appSign: String
)

class LiveStreamViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _liveSession = MutableStateFlow<LiveStreamSession?>(null)
    val liveSession: StateFlow<LiveStreamSession?> = _liveSession

    private val _isLive = MutableStateFlow(false)
    val isLive: StateFlow<Boolean> = _isLive

    /** Create a fresh session */
    fun prepareLiveSession() {
        _liveSession.value = generateNewSession()
        _isLive.value = false
    }



    /** Release resources completely */
    fun releaseSession() {
        _isLive.value = false
        _liveSession.value = null
    }

    /** Generate unique IDs for live session */
    private fun generateNewSession(): LiveStreamSession {
        val userID = "live_" + System.currentTimeMillis()
        val liveID = "live_" + UUID.randomUUID().toString().replace("-", "")
        val userName = tokenManager.getCachedUserId() ?: "UnKnown"

        val effectiveName = if (userName.isBlank() || userName.equals("Unknown", ignoreCase = true)) {
            userID
        } else {
            userName
        }

        return LiveStreamSession(
            userID = userID,
            userName = effectiveName,
            liveID = liveID,
            appID = ZegoConfig.APP_ID,
            appSign = ZegoConfig.APP_SIGN
        )
    }
}
