package com.example.aigiri.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.PermissionViewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

private fun openNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

@Composable
fun GrantPermissionScreen(
    viewModel: PermissionViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Permission states
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    val cameraPermissionGranted by viewModel.cameraPermissionGranted.collectAsState()
    val microphonePermissionGranted by viewModel.microphonePermissionGranted.collectAsState()
    val contactsPermissionGranted by viewModel.contactsPermissionGranted.collectAsState()
    val notificationsPermissionGranted by viewModel.notificationsPermissionGranted.collectAsState()

    // Launchers
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(Manifest.permission.ACCESS_FINE_LOCATION, granted)
        if (!granted) openAppSettings(context)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(Manifest.permission.CAMERA, granted)
        if (!granted) openAppSettings(context)
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(Manifest.permission.RECORD_AUDIO, granted)
        if (!granted) openAppSettings(context)
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(Manifest.permission.READ_CONTACTS, granted)
        if (!granted) openAppSettings(context)
    }

    val notificationsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            viewModel.onPermissionResult(Manifest.permission.POST_NOTIFICATIONS, granted)
            if (!granted) openNotificationSettings(context)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "Grant Permissions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF6A1B9A)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "We need a few permissions to ensure your safety features work instantly when needed. Please grant them now to avoid delay during emergencies.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                PermissionCard(
                    title = "Location",
                    granted = locationPermissionGranted,
                    onRequest = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )

                PermissionCard(
                    title = "Camera",
                    granted = cameraPermissionGranted,
                    onRequest = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                )

                PermissionCard(
                    title = "Microphone",
                    granted = microphonePermissionGranted,
                    onRequest = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                )

                PermissionCard(
                    title = "Contacts",
                    granted = contactsPermissionGranted,
                    onRequest = { contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS) }
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionCard(
                        title = "Notifications",
                        granted = notificationsPermissionGranted,
                        onRequest = { notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Setup complete!", duration = SnackbarDuration.Short)
                        }
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    enabled = viewModel.allPermissionsGranted()
                ) {
                    Text("Finish", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    granted: Boolean,
    onRequest: () -> Unit
) {
    val backgroundColor = if (granted) Color(0xFFF0F8F4) else Color(0xFFFFF8F8)
    val borderColor = if (granted) Color(0xFF4CAF50) else Color(0xFFB00020)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (granted) Color.Black else Color(0xFFB00020)
                )
                Text(
                    text = if (granted) "Granted" else "Tap to allow",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (granted) Color.Gray else Color.Red
                )
            }
            Button(
                onClick = onRequest,
                shape = RoundedCornerShape(8.dp),
                enabled = !granted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (granted) Color.Gray else Color(0xFF6A1B9A),
                    contentColor = Color.White
                )
            ) {
                Text(if (granted) "Granted" else "Allow")
            }
        }
    }
}
