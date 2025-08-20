package com.example.aigiri.repository
import android.content.Context
import com.example.aigiri.network.RetrofitInstance
import com.example.aigiri.model.LoginRequest
import com.example.aigiri.model.LoginResponse
import retrofit2.Response

class AuthRepository(private val context: Context) {
    private val api = RetrofitInstance.getApi(context)

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return api.login(request) // This is now correctly returning a Response<LoginResponse>
    }
}

