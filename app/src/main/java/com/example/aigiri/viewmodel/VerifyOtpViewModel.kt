package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.model.User
import com.example.aigiri.repository.OtpRepository
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OtpUiState(
    val otpInput: List<String> = List(6) { "" },
    val timeLeft: Int = 60,
    val error: String = "",
    val verificationId: String = ""
)

class VerifyOtpViewModel(
    private val otpRepository: OtpRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var tempUser: User? = null
    private var timerJob: Job? = null

    fun setTempUser(user: User) {
        tempUser = user
    }

    fun getTempUser(): User? = tempUser


    fun updateOtp(index: Int, value: String) {
        val newOtp = _uiState.value.otpInput.toMutableList()
        newOtp[index] = value
        _uiState.update { it.copy(otpInput = newOtp) }
    }

    fun setVerificationId(id: String) {
        _uiState.update { it.copy(verificationId = id, timeLeft = 60) } // Reset timeLeft too
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel() // Cancel any existing timer
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
        }
    }
    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun invalidateOtp() {
        _uiState.update { it.copy(verificationId = "", otpInput = List(6) { "" }) }
    }

    fun verifyOtp(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val enteredOtp = _uiState.value.otpInput.joinToString("")
        if (enteredOtp == _uiState.value.verificationId) {
            val user = tempUser
            if (user == null) {
                _uiState.update { it.copy(error = "User data missing") }
                onError("User data missing")
                return
            }

            viewModelScope.launch {
                val result = userRepository.saveUser(user)
                if (result.isSuccess) {
                    stopTimer()
                    onSuccess()
                } else {
                    val msg = result.exceptionOrNull()?.message ?: "Failed to save user"
                    _uiState.update { it.copy(error = msg) }
                    onError(msg)
                }
            }
        } else {
            _uiState.update { it.copy(error = "Incorrect OTP") }
            onError("Incorrect OTP")
        }
    }

    fun resendOtp(phoneNumber: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val newOtp = (100000..999999).random().toString()
            try {
                val response = otpRepository.sendOtp(phoneNumber, newOtp)
                if (response.isSuccess) {
                    _uiState.update {
                        it.copy(
                            timeLeft = 60,
                            error = "",
                            otpInput = List(6) { "" },
                            verificationId = newOtp
                        )
                    }
                    startTimer()
                    onSuccess(newOtp)
                } else {
                    _uiState.update { it.copy(error = "Failed to resend OTP") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Exception: ${e.localizedMessage}") }
            }
        }
    }
}