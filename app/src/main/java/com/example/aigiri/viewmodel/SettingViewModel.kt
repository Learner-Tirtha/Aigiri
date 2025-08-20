package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val showLogoutDialog: Boolean = false
)

class SettingsViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut

    fun onLogoutClick() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = true)
    }

    fun dismissLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
    }

    fun confirmLogout() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
        viewModelScope.launch {
            logout()
        }
    }


    private suspend fun logout() {
        tokenManager.clear()
        _loggedOut.value = true
    }
    fun resetLogoutFlag() {
        _loggedOut.value = false
    }
}
