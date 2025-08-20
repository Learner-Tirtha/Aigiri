package com.example.aigiri.service

import com.example.aigiri.model.ChatRequest
import com.example.aigiri.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {
    @POST("/chat") // <-- change to your actual ML endpoint path
    suspend fun getChatResponse(@Body request: ChatRequest): Response<ChatResponse>
}