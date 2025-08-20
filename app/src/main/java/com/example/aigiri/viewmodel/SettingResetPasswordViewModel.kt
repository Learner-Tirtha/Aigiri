package com.example.aigiri.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.network.TokenManager
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class PasswordResetState {
    object Idle : PasswordResetState()
    object Loading : PasswordResetState()
    object Success : PasswordResetState()
    data class Error(val message: String) : PasswordResetState()
}

class SettingResetPasswordViewModel(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var oldPassword = MutableStateFlow("")
    var newPassword = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")

    // Live userId from DataStore (null until loaded)
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _uiState = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val uiState: StateFlow<PasswordResetState> = _uiState

    init {
        // Load userId from DataStore asynchronously
        viewModelScope.launch {
            tokenManager.userIdFlow.collect { id ->
                Log.e("TokenManager", "Loaded userId: $id")
                _userId.value = id
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val currentUserId = _userId.value
            if (currentUserId.isNullOrEmpty()) {
                _uiState.value = PasswordResetState.Error("User ID not available")
                return@launch
            }

            val oldPass = oldPassword.value.trim()
            val newPass = newPassword.value.trim()
            val confirmPass = confirmPassword.value.trim()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                _uiState.value = PasswordResetState.Error("All fields are required")
                return@launch
            }

            if (newPass != confirmPass) {
                _uiState.value = PasswordResetState.Error("New passwords do not match")
                return@launch
            }

            if (oldPass == newPass) {
                _uiState.value = PasswordResetState.Error("New password must be different from old password")
                return@launch
            }

            _uiState.value = PasswordResetState.Loading

            // Validate old password
            val oldPasswordResult = repository.isPasswordValid(currentUserId, oldPass)
            if (oldPasswordResult.isFailure) {
                _uiState.value = PasswordResetState.Error(
                    oldPasswordResult.exceptionOrNull()?.message ?: "Error validating old password"
                )
                return@launch
            }
            if (oldPasswordResult.getOrNull() != true) {
                _uiState.value = PasswordResetState.Error("Old password is incorrect")
                return@launch
            }

            // Update password
            val updateResult = repository.updatePassword(currentUserId, newPass)
            if (updateResult.isSuccess) {
                _uiState.value = PasswordResetState.Success
            } else {
                _uiState.value = PasswordResetState.Error(
                    "Password update failed: ${updateResult.exceptionOrNull()?.message ?: "Unknown error"}"
                )
            }
        }
    }
}
