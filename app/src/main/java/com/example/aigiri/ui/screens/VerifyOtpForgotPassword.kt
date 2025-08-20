package com.example.aigiri.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.ForgotPasswordVerificationViewModel
import com.example.aigiri.viewmodel.ForgotPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpForgotPassword(
    navController: NavController,
    phoneNumber: String,
    verificationId: String,
    viewModel: ForgotPasswordVerificationViewModel,
    verifyOtpViewModel: ForgotPasswordViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // Set verification ID once
    LaunchedEffect(verificationId) {
        viewModel.setVerificationId(verificationId)
    }

    // Disable hardware back button
    BackHandler { /* do nothing */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify OTP") },
                navigationIcon = { /* No back button */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Verify OTP", fontSize = 24.sp)

            // Phone number row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sent to $phoneNumber", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.stopTimer()
                        viewModel.invalidateOtp()
                        verifyOtpViewModel.resetState()
                        navController.navigate("enter_phoneno") {
                            popUpTo("enter_phoneno") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit phone number",
                        tint = Color(0xFF6A1B9A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text("Time remaining: ${state.timeLeft} sec", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))

            // OTP fields
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                state.otpInput.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            if (it.length <= 1) {
                                viewModel.updateOtp(index, it)
                                when {
                                    it.isNotEmpty() && index < 5 -> focusRequesters[index + 1].requestFocus()
                                    it.isEmpty() && value.isNotEmpty() && index > 0 -> focusRequesters[index - 1].requestFocus()
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .size(50.dp)
                            .focusRequester(focusRequesters[index]),
                        singleLine = true,
                        enabled = state.timeLeft > 0
                    )
                    if (index < 5) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            if (state.error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(state.error, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    viewModel.verifyOtp(
                        onSuccess = {
                            navController.navigate("reset_password/${phoneNumber}")

                        },
                        onError = { println("Verification failed: $it") }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                enabled = state.timeLeft > 0
            ) {
                Text("Verify", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Resend OTP",
                modifier = Modifier.clickable(enabled = state.timeLeft == 0) {
                    if (state.timeLeft == 0) {
                        viewModel.resendOtp(phoneNumber) { newOtp ->
                            navController.navigate("verify_otp/$phoneNumber/$newOtp") {
                                popUpTo("verify_otp/$phoneNumber/$verificationId") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                color = if (state.timeLeft == 0) Color(0xFF6A1B9A) else Color.Gray
            )
        }
    }
}
