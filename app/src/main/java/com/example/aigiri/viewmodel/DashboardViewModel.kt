package com.example.aigiri.viewmodel


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Warning
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.example.aigiri.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NavigationItem(val name: String, val icon: ImageVector, val route: String)

class DashboardViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut
    val navigationItems = listOf(
        NavigationItem("Home", Icons.Filled.Home, "dashboard"),
        NavigationItem("Reports", Icons.Filled.MailOutline, "history"),
        NavigationItem("SOS", Icons.Filled.Warning, "sos"),
        NavigationItem("Chatbot", Icons.Filled.Home, "chatbot")
    )

    fun onReportClick(navController: NavHostController) {
        navController.navigate("report")
    }

    fun onSafeWalkClick(navController: NavHostController) {
        navController.navigate("livetracking")
    }
    fun logout() {
        viewModelScope.launch {
            tokenManager.clear()
            _loggedOut.value = true
        }
    }
}
