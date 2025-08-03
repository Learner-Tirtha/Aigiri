package com.example.aigiri.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsPower
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigiri.viewmodel.DashboardViewModel
import com.example.aigiri.R
import com.example.aigiri.network.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun TopNavBar(
    title: String = "Aigiri",
    viewModel: DashboardViewModel,
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val primaryPurple = Color(0xFF6A1B9A)
    val token by tokenManager.tokenFlow.collectAsState(initial = null)

    // Only this handles navigation when token is gone
    LaunchedEffect(token) {
        if (token == null) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Clears backstack
            }
        }
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.durga),
                    contentDescription = "Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            IconButton(onClick = {
              navController.navigate("setting")
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Logout",
                    tint = primaryPurple
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}
