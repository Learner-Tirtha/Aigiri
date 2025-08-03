//package com.example.aigiri.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.aigiri.ui.components.isValidPassword
//import com.example.aigiri.ui.components.passwordWarning
//import com.example.aigiri.viewmodel.ForgotPasswordViewModel
//import kotlinx.coroutines.delay
//
//@Composable
//fun ResetPasswordScreen(
//    navController: NavController,
//    viewModel: ForgotPasswordViewModel = viewModel()
//) {
//    val snackbarHostState = remember { SnackbarHostState() }
//    val newPassword by viewModel.newPassword
//    val confirmPassword by viewModel.confirmPassword
//    val showSuccessMessage by remember { derivedStateOf { viewModel.showSuccessMessage } }
//
//    LaunchedEffect(showSuccessMessage) {
//        if (showSuccessMessage) {
//            snackbarHostState.showSnackbar("Password Reset successfully")
//            delay(1000L)
//            viewModel.onSuccessMessageShown()
//            navController.navigate("login") {
//                popUpTo("forgot_password") { inclusive = true } // optional: remove from backstack
//            }
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { padding ->
//        var newPasswordVisible by remember { mutableStateOf(false) }
//        var confirmPasswordVisible by remember { mutableStateOf(false) }
//
//        val isValid = newPassword.isNotBlank() &&
//                confirmPassword.isNotBlank() &&
//                newPassword == confirmPassword &&
//                isValidPassword(newPassword) &&
//                !viewModel.isSameAsOldPassword()
//
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(24.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Reset Password", fontSize = 26.sp, color = Color(0xFF6A1B9A))
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//
//            OutlinedTextField(
//                value = newPassword,
//                onValueChange = viewModel::onNewPasswordChange,
//                label = { Text("New Password") },
//                singleLine = true,
//                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val icon = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
//                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
//                        Icon(imageVector = icon, contentDescription = null)
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp),
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Password,
//                    imeAction = ImeAction.Next
//                )
//            )
//            val warningMessage = passwordWarning(newPassword)
//            if (warningMessage.isNotEmpty()) {
//                Text(warningMessage, color = Color.Red, fontSize = 12.sp)
//            }
//            val sameAsOldPassword = viewModel.isSameAsOldPassword()
//            if (sameAsOldPassword) {
//                Text(
//                    "New password must not be the same as old password",
//                    color = Color.Red,
//                    fontSize = 12.sp
//                )
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = confirmPassword,
//                onValueChange = viewModel::onConfirmPasswordChange,
//                label = { Text("Confirm Password") },
//                singleLine = true,
//                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
//                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
//                        Icon(imageVector = icon, contentDescription = null)
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp),
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Password,
//                    imeAction = ImeAction.Done
//                )
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Button(
//                onClick = viewModel::sendReset,
//                enabled = isValid,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
//            ) {
//                Text("Reset Password", color = Color.White, fontSize = 16.sp)
//            }
//
//
//
//        }
//    }
//}
