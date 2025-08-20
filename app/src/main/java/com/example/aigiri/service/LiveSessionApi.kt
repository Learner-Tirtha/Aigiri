package com.example.aigiri.service

import com.example.aigiri.model.StartSessionRequest
import com.example.aigiri.model.StartSessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface LiveSessionApi {
    @POST("/start-session")
    suspend fun startSession(@Body request: StartSessionRequest): Response<StartSessionResponse>
}
