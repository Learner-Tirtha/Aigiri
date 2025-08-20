package com.example.aigiri.service

import com.example.aigiri.model.LoginRequest
import com.example.aigiri.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
