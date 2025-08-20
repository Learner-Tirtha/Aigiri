package com.example.aigiri.ui.screens


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.VerifyOtpViewModel
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import com.example.aigiri.repository.UserRepository
import com.example.aigiri.viewmodel.SignupViewModel
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpScreen(
    navController: NavController,
    phoneNumber: String,
    verificationId: String,
    viewModel: VerifyOtpViewModel,
    signupViewModel: SignupViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val focusRequesters = remember { List(6) { FocusRequester() } }
//    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(verificationId) {
        viewModel.setVerificationId(verificationId)
    }
    //Added
    BackHandler {
        viewModel.invalidateOtp()
        viewModel.getTempUser()?.let {
            signupViewModel.setTempUser(it)
        }
        signupViewModel.resetState(clearTempUser = false)
        navController.navigate("signup") {
            popUpTo("signup") { inclusive = true }
            launchSingleTop = true
        }
    }


    //TopAppBar Added
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify OTP") },
                navigationIcon = {
//                    IconButton(onClick = {
//                        viewModel.invalidateOtp()
//                        navController.popBackStack()
//                    }
//                    ) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }

                }
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
            Text("Sent to +91$phoneNumber", fontSize = 16.sp, color = Color.Gray)
            Text("Time remaining: ${state.timeLeft} sec", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

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
                    val tempUser = signupViewModel.getTempUser()
                    if (tempUser != null) {
                        viewModel.setTempUser(tempUser)

                        viewModel.verifyOtp(
                            onSuccess = {
                                signupViewModel.resetState()
                                navController.navigate("permission") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            },
                            onError = {
                                println("Verification failed: $it")
                            }
                        )
                    } else {
                        println("Temp user is null. Cannot verify or save.")
                    }
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
                            }
                        }
                    }
                },
                color = if (state.timeLeft == 0) Color(0xFF6A1B9A) else Color.Gray
            )
        }
    }
}
