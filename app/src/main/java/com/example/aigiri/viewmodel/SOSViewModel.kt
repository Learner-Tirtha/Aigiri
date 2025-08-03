package com.example.aigiri.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.model.SOSRequest
import com.example.aigiri.network.TokenManager
import com.example.aigiri.repository.EmergencyContactsRepository
import com.example.aigiri.repository.SOSRepository
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.launch

class SOSViewModel(
    private val repository: SOSRepository,
    private val emergencyRepository: EmergencyContactsRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _sosStatus = MutableLiveData<Result<Unit>>()
    val sosStatus: LiveData<Result<Unit>> = _sosStatus

    fun sendSOS(lat: Double, lon: Double) {
        viewModelScope.launch {
            val uid = tokenManager.getCachedUserId()
            if (uid == null) {
                _sosStatus.postValue(Result.failure(Exception("❌ UID not found")))
                return@launch
            }
            val userPhoneNumber: String = userRepository.fetchPhoneNoByuserID(uid)
            val message = "🚨 SOS! I'm in danger.\nPhone: $userPhoneNumber\nLocation: https://www.google.com/maps?q=${lat},${lon}"


            if (repository.isInternetAvailable()) {
                val result = repository.sendSOSOnline(
                    SOSRequest(uid, message)

                )
                Log.e("SOS","Internet aviable go for SendSOS")
                _sosStatus.postValue(result)
            } else {
                val contactsResult = emergencyRepository.getEmergencyContacts(uid)
                val contacts = contactsResult.getOrElse {
                    _sosStatus.postValue(Result.failure(it))
                    return@launch
                }
                val phoneNumbers = contacts.map { it.phoneNumber }
                repository.sendSOSViaSms(phoneNumbers, message)
                _sosStatus.postValue(Result.success(Unit))
            }
        }
    }
}
