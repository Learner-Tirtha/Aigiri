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

    private val _liveSession = MutableStateFlow(generateNewSession())
    val liveSession: StateFlow<LiveStreamSession> = _liveSession

    private fun generateNewSession(): LiveStreamSession {


        val userID = "live_" + System.currentTimeMillis()
        val liveID = "live_" + UUID.randomUUID().toString().replace("-", "")
        val userName = tokenManager.getCachedUserId() ?: "UnKnown"

        return LiveStreamSession(
            userID = userID,
            userName = userName,
            liveID = liveID,
            appID = ZegoConfig.APP_ID,
            appSign = ZegoConfig.APP_SIGN
        )
    }

    fun prepareLiveSession() {
        _liveSession.value = generateNewSession()
    }
}
