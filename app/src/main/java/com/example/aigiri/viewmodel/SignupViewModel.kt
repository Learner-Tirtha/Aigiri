package com.example.aigiri.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt

import com.example.aigiri.model.User
import com.example.aigiri.repository.OtpRepository
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



sealed class SendOtpUiState {
    object Idle : SendOtpUiState()
    object Loading : SendOtpUiState()
    data class Success(val phoneNumber: String, val otp: String) : SendOtpUiState()
    data class Error(val message: String) : SendOtpUiState()
}

class SignupViewModel(
    private val otpRepository: OtpRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SendOtpUiState>(SendOtpUiState.Idle)
    val uiState: StateFlow<SendOtpUiState> = _uiState
    private var tempUser: User? = null
        private set

    fun setTempUser(user: User) {
        tempUser = user
    }
    fun resetState(clearTempUser: Boolean = true) {
        _uiState.value = SendOtpUiState.Idle
        usernameTaken.value = false
        emailTaken.value = false
        phoneTaken.value = false
        if (clearTempUser) tempUser = null
    }


    fun getTempUser(): User? = tempUser
    val usernameTaken = MutableStateFlow(false)
    val emailTaken = MutableStateFlow(false)
    val phoneTaken = MutableStateFlow(false)



    private fun generateOtp(): String = (100000..999999).random().toString()

    fun checkUsername(username: String) {
        viewModelScope.launch {
            userRepository.isUsernameTaken(username).onSuccess {
                usernameTaken.value = it
            }
        }
    }

    fun checkEmail(email: String) {
        if (email.isBlank()) {
            emailTaken.value = false
            return
        }
        viewModelScope.launch {
            userRepository.isEmailTaken(email).onSuccess {
                emailTaken.value = it
            }
        }
    }

    fun checkPhone(phone: String) {
        // Validate format locally
        val isValid =
            phone.startsWith("+") && phone.length == 13 && phone.drop(1).all { it.isDigit() }

        if (!isValid) {
            phoneTaken.value = false // Clear existing state
            return
        }

        // Check availability only if format is valid
        viewModelScope.launch {
            userRepository.isPhoneTaken(phone).onSuccess {
                phoneTaken.value = it
            }
        }
    }
    fun sendOtp(username: String, password: String, phone: String, email: String?) {
        val isValid = phone.startsWith("+") && phone.length == 13 && phone.drop(1).all { it.isDigit() }

        if (!isValid) {
            _uiState.value = SendOtpUiState.Error("Enter a valid phone number")
            return
        }

        val otp = generateOtp()
        _uiState.value = SendOtpUiState.Loading

        viewModelScope.launch {
            try {
                val result = otpRepository.sendOtp(phone, otp)
                if (result.isSuccess) {
                    // Hash the password
                    val hashedPassword = BCrypt.withDefaults().hashToString(10, password.toCharArray())

                    setTempUser(User(username, phone,hashedPassword, email))
                    _uiState.value = SendOtpUiState.Success(phone, otp)
                } else {
                    _uiState.value = SendOtpUiState.Error(result.exceptionOrNull()?.message ?: "Failed to send OTP")
                }
            } catch (e: Exception) {
                _uiState.value = SendOtpUiState.Error(e.message ?: "Unknown error")
            }
        }
    }


}






