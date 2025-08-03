package com.example.aigiri.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aigiri.R

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.durga),
            contentDescription = "Shield Icon",
            modifier = Modifier.size(80.dp) // Slightly larger for emphasis
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome to SafeDisclose",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF471366)
        )
        Text(
            text = "Your safety, your voice",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoCard("Report workplace incidents safely")
            InfoCard("Stay protected with SafeWalk")
            InfoCard("Get legal help when you need it")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A)
            )
        ) {
            Text(text = "Get Started", fontSize = 18.sp)
        }
    }
}

@Composable
fun InfoCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Bullet",
            tint = Color(0xFF6A1B9A),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 16.sp)
    }
}
