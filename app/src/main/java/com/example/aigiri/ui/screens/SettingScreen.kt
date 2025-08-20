package com.example.aigiri.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aigiri.viewmodel.SettingsViewModel

data class SettingsItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val action: () -> Unit,
    val trailingText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedOut by viewModel.loggedOut.collectAsState()

    val primaryPurple = Color(0xFF6A1B9A)
    val lightPurple = Color(0xFFF3E5F5)

    // React to logout state change
    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            viewModel.resetLogoutFlag()
        }

    }

    val settingsItems = listOf(
        SettingsItem(
            "Emergency Contacts",
            { Icon(Icons.Default.AccountBox, contentDescription = null, tint = primaryPurple) },
            { navController.navigate("add_contacts") },
            "Edit"
        ),
        SettingsItem(
            "Email",
            { Icon(Icons.Default.Email, contentDescription = null, tint = primaryPurple) },
            { navController.navigate("safeZones") },
            "Add/Change"
        ),
        SettingsItem(
            "Reset Password",
            { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryPurple) },
            { navController.navigate("setting_reset_password") },
            "Manage"
        ),
        SettingsItem(
            "HelpLine",
            { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryPurple) },
            { navController.navigate("national_helpline") },
            "Call Now"
        ),
        SettingsItem(
            "Log Out",
            { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = primaryPurple) },
            { viewModel.onLogoutClick() },
            "Sign out"
        )

    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            settingsItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { item.action() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(lightPurple, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                item.icon()
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = item.title, fontSize = 16.sp, color = Color.Black)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            item.trailingText?.let {
                                Text(it, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = primaryPurple
                            )
                        }
                    }
                }
            }


            if (uiState.showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissLogoutDialog() },
                    title = { Text("Log Out") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.confirmLogout() // no need to pass lambda anymore
                            }
                        ) {
                            Text("Yes", color = primaryPurple)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.dismissLogoutDialog() }) {
                            Text("No", color = primaryPurple)
                        }
                    }
                )
            }
        }
    }
}



