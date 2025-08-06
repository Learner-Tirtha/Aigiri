package com.example.aigiri.network

import com.example.aigiri.service.SOSApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SOSApiClient {
    private const val BASE_URL = "http://192.168.1.7:3001/"

    val api: SOSApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SOSApiService::class.java)
    }
}
