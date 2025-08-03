package com.example.aigiri.service


import com.example.aigiri.model.SOSRequest
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST


interface SOSApiService {
    @POST("/send-sos")
    suspend fun sendSOS(@Body sosRequest: SOSRequest): Response<Unit>
}
