package com.example.aigiri.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.aigiri.network.TokenManager
import com.example.aigiri.ui.screens.SplashScreen


@Composable
fun rememberTokenManager(): TokenManager {
    val context = LocalContext.current
    return remember { TokenManager(context) }
}

@Composable
fun RootApp() {
    val tokenManager = rememberTokenManager()
    val token by tokenManager.tokenFlow.collectAsState(initial = null)

    val startDestination = if (token.isNullOrEmpty()) "splash" else "dashboard"

    AppNavigation(startDestination = startDestination, tokenManager = tokenManager)
}