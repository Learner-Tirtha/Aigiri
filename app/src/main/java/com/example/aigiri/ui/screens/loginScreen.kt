package com.example.aigiri.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aigiri.network.TokenManager
import com.example.aigiri.viewmodel.LoginUiState
import com.example.aigiri.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    tokenManager: TokenManager
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()

    val isValid = identifier.isNotBlank() && password.isNotBlank()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 26.sp, color = Color(0xFF6A1B9A))

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it.trim() },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val desc = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = desc)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Forgot Password?",
            color = Color(0xFF6A1B9A),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    navController.navigate("forgot_password") // Implement this route
                },
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                viewModel.login(identifier, password)
            },
            enabled = isValid && loginState !is LoginUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", color = Color.White, fontSize = 16.sp)
        }

        if (loginState is LoginUiState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (loginState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (loginState as LoginUiState.Error).message,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        val token by tokenManager.tokenFlow.collectAsState(initial = null)

        LaunchedEffect(loginState, token) {
            when (loginState) {
                is LoginUiState.Success -> {
                    if (token != null) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                        viewModel.resetState()
                    }
                }

                is LoginUiState.NavigateToEmergencyContact -> {
                    navController.navigate("add_contacts") {
                        popUpTo("login") { inclusive = true }
                    }
                    viewModel.resetState()
                }

                else -> Unit
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Create an Account",
            color = Color(0xFF6A1B9A),
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                navController.navigate("signup")
            },
            textDecoration = TextDecoration.Underline
        )
    }
}
