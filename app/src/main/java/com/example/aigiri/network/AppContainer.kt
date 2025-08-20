package com.example.aigiri.network

// AppContainer.kt
object AppContainer {
    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl("http://192.168.1.3:3000/")
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val otpApiService = retrofit.create(com.example.aigiri.service.OtpApiService::class.java)

}
