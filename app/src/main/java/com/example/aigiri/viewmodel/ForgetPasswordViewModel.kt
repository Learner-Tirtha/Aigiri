package com.example.aigiri.viewmodel

import com.example.aigiri.repository.OtpRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ForgotPasswordViewModel(
    private val otpRepository: OtpRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val _sendOtpState = MutableStateFlow<SendOtpUiState>(SendOtpUiState.Idle)
    val sendOtpState: StateFlow<SendOtpUiState> = _sendOtpState

    fun sendOtp(phoneNumber: String) {
        val otp = (100000..999999).random().toString() // Generate random 6-digit OTP
        _sendOtpState.value = SendOtpUiState.Loading

        viewModelScope.launch {
            val result = otpRepository.sendOtp(phoneNumber, otp)
            _sendOtpState.value = if (result.isSuccess) {
                SendOtpUiState.Success(phoneNumber,otp)
            } else {
                SendOtpUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _sendOtpState.value = SendOtpUiState.Idle
    }
    fun checkPhoneExists(phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(userRepository.isPhoneTaken(phone).getOrDefault(false))
        }
    }

    fun setError(message: String) {
        _sendOtpState.value = SendOtpUiState.Error(message)
    }
}

