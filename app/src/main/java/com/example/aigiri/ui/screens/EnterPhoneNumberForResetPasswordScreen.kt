package com.example.aigiri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.ForgotPasswordViewModel
import com.example.aigiri.viewmodel.SendOtpUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun forgotPasswordPhonenoScreen(
    navController: NavController,
    verifyOtpViewModel: ForgotPasswordViewModel
) {
    var phoneNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCountryCode by remember { mutableStateOf("+91") }

    val countryCodes = listOf("+91", "+1", "+44", "+61")
    val keyboardController = LocalSoftwareKeyboardController.current
    val isValidPhone = phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }

    val sendOtpState by verifyOtpViewModel.sendOtpState.collectAsState()

    LaunchedEffect(Unit) {
        phoneNumber = ""
        selectedCountryCode = "+91"
        verifyOtpViewModel.resetState()
    }

    // ✅ Navigate only on success
    LaunchedEffect(sendOtpState) {
        if (sendOtpState is SendOtpUiState.Success) {
            val success = sendOtpState as SendOtpUiState.Success
            navController.navigate("forgot_password_verify_otp/${success.phoneNumber}/${success.otp}") {
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Login"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Enter Phone Number", fontSize = 26.sp, color = Color(0xFF6A1B9A))

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country code dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.width(100.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCountryCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Code") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .width(100.dp)
                            .padding(start = 4.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    selectedCountryCode = code
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 10) {
                            phoneNumber = input
                        }
                    },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()

                    // Check if number exists before sending OTP
                    verifyOtpViewModel.checkPhoneExists("${selectedCountryCode}$phoneNumber") { exists ->
                        if (exists) {
                            verifyOtpViewModel.sendOtp("${selectedCountryCode}$phoneNumber")
                        } else {
                            verifyOtpViewModel.setError("No account found with this phone number")
                        }
                    }
                },
                enabled = isValidPhone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue", color = Color.White, fontSize = 16.sp)
            }

            if (phoneNumber.isNotEmpty() && !isValidPhone) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Phone number must be exactly 10 digits",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            if (sendOtpState is SendOtpUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (sendOtpState as SendOtpUiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}
