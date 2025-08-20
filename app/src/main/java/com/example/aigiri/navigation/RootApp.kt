package com.example.aigiri.navigation


import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.aigiri.Trigger_SOS.WakeWordService
import com.example.aigiri.network.TokenManager
import com.example.aigiri.ui.screens.SplashScreen



@Composable
fun rememberTokenManager(): TokenManager {
    val context = LocalContext.current
    return remember { TokenManager(context) }
}

@Composable
fun RootApp() {
    val context = LocalContext.current
    val tokenManager = rememberTokenManager()
    val token by tokenManager.tokenFlow.collectAsState(initial = null)

    val startDestination = if (token.isNullOrEmpty()) "splash" else "dashboard"

    // Start WakeWordService when token is available (user is logged in)
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            val intent = Intent(context, WakeWordService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    AppNavigation(startDestination = startDestination, tokenManager = tokenManager)
}
