package com.example.aigiri.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.launch
import at.favre.lib.crypto.bcrypt.BCrypt

class ResetPasswordViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val success: Boolean = false,
        val showSuccessScreen: Boolean = false,
        val errorMessage: String = ""
    )

    var uiState = mutableStateOf(UiState())
        private set

    fun isPasswordValid(password: String): Boolean {
        val minLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return minLength && hasUpperCase && hasLowerCase && hasSpecialChar
    }

    fun resetPassword(phoneNumber: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            uiState.value = UiState(isLoading = true)

            // Validation
            if (!isPasswordValid(newPassword)) {
                uiState.value = UiState(
                    errorMessage = "Password must be at least 8 characters, include uppercase, lowercase, and a special character."
                )
                return@launch
            }

            if (newPassword != confirmPassword) {
                uiState.value = UiState(
                    errorMessage = "Passwords do not match."
                )
                return@launch
            }

            try {
                // Hash the password
                val hashedPassword = BCrypt.withDefaults()
                    .hashToString(10, newPassword.toCharArray())

                // Update password in DB
                val result = userRepository.updatePasswordByPhone(phoneNumber, hashedPassword)

                if (result.isSuccess) {
                    uiState.value = UiState(success = true, showSuccessScreen = true)
                } else {
                    uiState.value = UiState(
                        errorMessage = result.exceptionOrNull()?.message ?: "Password update failed"
                    )
                }
            } catch (e: Exception) {
                uiState.value = UiState(errorMessage = e.message ?: "Something went wrong")
            }
        }
    }
}
