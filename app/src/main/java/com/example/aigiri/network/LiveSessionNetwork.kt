package com.example.aigiri.network

import com.example.aigiri.service.LiveSessionApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private const val BASE_URL = "http://10.79.73.249:3001/" // Change this

    val liveSessionApi: LiveSessionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LiveSessionApi::class.java)
    }
}
