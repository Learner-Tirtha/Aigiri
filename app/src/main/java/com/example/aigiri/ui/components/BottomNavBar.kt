package com.example.aigiri.ui.components

import android.content.Context
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
import com.example.aigiri.viewmodel.SOSViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

data class NavigationItem(val name: String, val icon: ImageVector, val route: String)

private val bottomNavLeftItems = listOf(
    NavigationItem("Home", Icons.Filled.Home, "dashboard"),
    NavigationItem("History", Icons.Filled.MailOutline, "history")
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
