package com.example.aigiri.ui.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.example.aigiri.R
import com.example.aigiri.viewmodel.SOSViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

data class NavigationItem(val name: String, val icon: ImageVector, val route: String)

private val bottomNavLeftItems = listOf(
    NavigationItem("Home", Icons.Filled.Home, "dashboard"),
    NavigationItem("history", Icons.Filled.MailOutline, "history")
)

private val bottomNavRightItems = listOf(
    NavigationItem("Chatbot", Icons.Filled.Hub, "chatbot"),
    NavigationItem("Live", Icons.Filled.LiveTv, "livecall")
)

@Composable
fun BottomNavBar(
    navController: NavHostController,
    viewModel: SOSViewModel,
    context: Context,
    primaryColor: Color = Color(0xFF6A1B9A)
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            containerColor = Color.White,
            tonalElevation = 10.dp
        ) {
            bottomNavLeftItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.name) },
                    label = { Text(item.name) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (item.route == "history") {
                            // Handle history click with biometric auth
                            val activity = context as? FragmentActivity
                            if (activity != null) {
                                val authHelper = BiometricAuthHelper(context)
                                if (authHelper.isBiometricAvailable()) {
                                    authHelper.authenticate(
                                        activity = activity,
                                        onSuccess = {
                                            // Navigate to history on success
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        onError = { error ->
                                            // Show error message
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = error,
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        },
                                        onFailed = {
                                            // Show authentication failed message
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.authentication_failed),
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    )
                                } else {
                                    // Biometric not available, navigate directly
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        } else {
                            // For other items, navigate directly
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = primaryColor,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.White
                    )
                )
            }



            Spacer(modifier = Modifier.width(60.dp)) // Reserve space for SOS button

            bottomNavRightItems.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.name) },
                    label = { Text(item.name) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = primaryColor,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.White
                    )
                )
            }
        }

        AnimatedSosButton(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp),
            viewModel = viewModel,
            context = context
        )
    }
}

@Composable
fun AnimatedSosButton(
    modifier: Modifier,
    viewModel: SOSViewModel,
    context: Context
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var isPressed by remember { mutableStateOf(false) }

    // (Removed LiveData interop to avoid unresolved refs)

    // Notification permission launcher for Android 13+
    val notifPermissionLauncher = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(context, "Please allow notifications for live tracking", Toast.LENGTH_SHORT).show()
            }
        }
    } else null

    LaunchedEffect(isPressed) {
        if (isPressed) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                val tokenSource = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    tokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.sendSOS(location.latitude, location.longitude)
                        // Request notification permission on Android 13+ if not granted
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val hasNotif = ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (!hasNotif) {
                                notifPermissionLauncher?.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        // Open owner live tracking screen using ViewModel UID
                        Log.d("SOS", "Location: ${location.latitude}, ${location.longitude}")
                    } else {
                        Toast.makeText(context, "❌ Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "⚠ Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "📍 Location permission denied", Toast.LENGTH_SHORT).show()
            }

            isPressed = false
        }
    }

    val buttonColor = if (isPressed) Color(0xFF4CAF50) else Color.Red

    Box(
        modifier = modifier
            .size(70.dp)
            .drawBehind {
                drawCircle(
                    color = buttonColor.copy(alpha = pulseAlpha),
                    radius = size.minDimension / 2 + 12,
                    style = Stroke(width = 8f)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        FloatingActionButton(
            onClick = { isPressed = true },
            containerColor = buttonColor,
            contentColor = Color.White,
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Text("SOS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}