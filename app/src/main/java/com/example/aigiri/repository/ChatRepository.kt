package com.example.aigiri.repository



import com.example.aigiri.model.ChatRequest
import com.example.aigiri.model.ChatResponse
//import com.example.aigiri.network.Chatbot.api
import com.example.aigiri.service.ChatApiService
import retrofit2.Response

class ChatRepository(
) {
    suspend fun getChatResponse(request: ChatRequest): Response<ChatResponse> {
        return Response.success(ChatResponse("I'm a future AI response to: ${request.question}"))
    }
}