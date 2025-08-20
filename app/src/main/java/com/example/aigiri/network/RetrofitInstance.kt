package com.example.aigiri.network

import android.content.Context
import com.example.aigiri.service.AuthApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    @Volatile
    private var api: AuthApi? = null

    fun getApi(context: Context): AuthApi {
        return api ?: synchronized(this) {
            val tokenManager = TokenManager(context.applicationContext)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val createdApi = retrofit.create(AuthApi::class.java)
            api = createdApi
            createdApi
        }
    }

    private const val BASE_URL = "http://192.168.1.3:3001/"
}
