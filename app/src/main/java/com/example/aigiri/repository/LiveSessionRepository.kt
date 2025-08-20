package com.example.aigiri.repository
import android.net.Uri
import com.example.aigiri.model.LiveSession
import com.example.aigiri.model.StartSessionRequest
import com.example.aigiri.network.TokenManager
import com.example.aigiri.service.LiveSessionApi
import kotlinx.coroutines.flow.first
import com.example.aigiri.model.StartSessionResponse

class LiveSessionRepository(
    private val api: LiveSessionApi,
    private val tokenManager: TokenManager
) {
    suspend fun startLiveSession(): LiveSession? {
        val token = tokenManager.tokenFlow.first()
        val userId = tokenManager.userIdFlow.first()

        if (token == null || userId == null) return null

        val response = api.startSession(StartSessionRequest(jwt = token, userId = userId))

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val uri = Uri.parse(body.joinUrl)
                val tokenFromUrl = uri.getQueryParameter("token") ?: return null
                val wsUrl = "${uri.scheme}://${uri.host}"

                return LiveSession(
                    userId = userId,
                    joinUrl = body.joinUrl,
                    token = tokenFromUrl,
                    wsUrl = wsUrl,
                    timestamp = System.currentTimeMillis(),
                    recordingUrl = null
                )
            } else null
        } else {
            // Optional: log error body
            val errorText = response.errorBody()?.string()
            println("Start session failed: $errorText")
            null
        }
    }

}
