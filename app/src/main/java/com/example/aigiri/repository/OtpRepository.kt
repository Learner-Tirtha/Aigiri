package com.example.aigiri.repository

import com.example.aigiri.network.AppContainer.otpApiService
import com.example.aigiri.service.OtpApiService
import com.example.aigiri.model.OtpRequest

class OtpRepository() {
    suspend fun sendOtp(phoneNumber: String, otp: String): Result<Unit> {
        return try {
            val response = otpApiService.sendOtp(OtpRequest(phoneNumber, otp))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

