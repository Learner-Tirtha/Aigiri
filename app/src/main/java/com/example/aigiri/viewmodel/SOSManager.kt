package com.example.aigiri.viewmodel


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.aigiri.model.SOSRequest
import com.example.aigiri.network.TokenManager
import com.example.aigiri.repository.EmergencyContactsRepository
import com.example.aigiri.repository.SOSRepository
import com.example.aigiri.repository.UserRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SOSManager(
    private val context: Context,
    private val sosRepository: SOSRepository,
    private val emergencyRepository: EmergencyContactsRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) {
    fun triggerSOS() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            Toast.makeText(context, "📍 Location permission denied", Toast.LENGTH_SHORT).show()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val tokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            tokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                sendSOS(location.latitude, location.longitude)
            } else {
                Toast.makeText(context, "❌ Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "⚠ Failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSOS(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val uid = tokenManager.getCachedUserId()
            if (uid == null) {
                Log.e("SOSManager", "❌ UID not found")
                return@launch
            }

            val userPhoneNumber = userRepository.fetchPhoneNoByuserID(uid)
            val message =
                "🚨 SOS! I'm in danger.\nPhone: $userPhoneNumber\nCurrent location: https://www.google.com/maps?q=${lat},${lon}"

            if (sosRepository.isInternetAvailable()) {
                val result = sosRepository.sendSOSOnline(SOSRequest(uid, message))
                Log.d("SOSManager", "✅ SOS sent online: $result")
                // Start live location streaming in foreground service

            } else {
                val contactsResult = emergencyRepository.getEmergencyContacts(uid)
                val contacts = contactsResult.getOrElse {
                    Log.e("SOSManager", "❌ Failed to fetch contacts: ${it.message}")
                    return@launch
                }
                val phoneNumbers = contacts.map { it.phoneNumber }
                sosRepository.sendSOSViaSms(phoneNumbers, message)
                // Start live location streaming in foreground service even if offline (SMS sent)

                Log.d("SOSManager", "✅ SOS sent via SMS")
            }
        }
    }
}