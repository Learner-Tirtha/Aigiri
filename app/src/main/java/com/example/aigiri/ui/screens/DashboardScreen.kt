package com.example.aigiri.ui.screens



import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aigiri.network.TokenManager
import com.example.aigiri.ui.components.BottomNavBar
import com.example.aigiri.ui.components.TopNavBar
import com.example.aigiri.viewmodel.DashboardViewModel
import com.example.aigiri.viewmodel.LiveStreamViewModel
import com.example.aigiri.viewmodel.SOSViewModel


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navController: NavHostController,
    tokenManager: TokenManager,
    liveStreamViewModel: LiveStreamViewModel,
    SOSViewModel:SOSViewModel,
    Context: Context
) {
    val primaryPurple = Color(0xFF6A1B9A)
    val lightPurple = Color(0xFFF1E6FF)
    Scaffold(
        topBar = {
            TopNavBar(title = "Aigiri",viewModel=viewModel, navController=navController, tokenManager=tokenManager)
        },
        bottomBar = {
            BottomNavBar(navController,viewModel=SOSViewModel, context =Context)
        }
    ) { paddingValues ->
        DashboardContent(
            paddingValues = paddingValues,
            primaryPurple = primaryPurple,
            lightPurple = lightPurple,
            onReportClick = { viewModel.onReportClick(navController) },
            onSafeWalkClick = { viewModel.onSafeWalkClick(navController) }
        )
    }
}

@Composable
fun DashboardContent(
    paddingValues: PaddingValues,
    primaryPurple: Color,
    lightPurple: Color,
    onReportClick: () -> Unit,
    onSafeWalkClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = primaryPurple
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Your safety is our priority",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                        .width(90.dp)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                )
            }
        }

        // Workplace Safety Option
        SafetyOption(
            icon = Icons.Filled.Settings,
            title = "Report Generator",
            description = "Report workplace incidents anonymously and securely",
            buttonText = "Report Now",
            primaryColor = primaryPurple,
            backgroundColor = lightPurple,
            onButtonClick = onReportClick
        )

        // Physical Safety Option
        SafetyOption(
            icon = Icons.Filled.Settings,
            title = "Safe Walk",
            description = "Use SafeWalk when traveling to stay protected",
            buttonText = "Start SafeWalk",
            primaryColor = primaryPurple,
            backgroundColor = lightPurple,
            onButtonClick = onSafeWalkClick
        )
        Spacer(modifier = Modifier.height(2.dp))

//        LiveButton(viewModel = liveStreamViewModel,navController)

    }
}

@Composable
fun SafetyOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonText: String,
    primaryColor: Color,
    backgroundColor: Color,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                )
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}