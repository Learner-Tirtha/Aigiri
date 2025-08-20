package com.example.aigiri.service
import com.example.aigiri.model.OtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpApiService {
    @POST("send-otp")
    suspend fun sendOtp(@Body otpRequest: OtpRequest): Response<Unit>
}



