package com.example.aigiri.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.SmsManager
import android.util.Log
import com.example.aigiri.model.SOSRequest
import com.example.aigiri.network.SOSApiClient.api

class SOSRepository(
    private val context: Context
) {

    fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        Log.e("SOS","Internet Check")
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun sendSOSOnline(request: SOSRequest): Result<Unit> {
        Log.e("SOS","Call to send ")
        return runCatching {
            api.sendSOS(request)
        }.map { it.body() ?: Unit }
    }

    fun sendSOSViaSms(contacts: List<String>, message: String) {
        val smsManager = SmsManager.getDefault()
        contacts.forEach {
            smsManager.sendTextMessage(it, null, message, null, null)
        }
    }
}
