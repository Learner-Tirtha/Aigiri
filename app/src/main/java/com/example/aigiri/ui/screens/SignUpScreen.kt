package com.example.aigiri.ui.screens
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.aigiri.ui.components.isValidPassword
import com.example.aigiri.ui.components.passwordWarning
import com.example.aigiri.viewmodel.SendOtpUiState
import com.example.aigiri.viewmodel.SignupViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignupViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("+91") }

    val isPasswordValid = isValidPassword(password)
    val state by viewModel.uiState.collectAsState()
    val usernameTaken by viewModel.usernameTaken.collectAsState()
    val emailTaken by viewModel.emailTaken.collectAsState()
    val phoneTaken by viewModel.phoneTaken.collectAsState()

    val isUsernameValid = username.isNotBlank()
    val isConfirmPasswordValid = confirmPassword == password && confirmPassword.isNotBlank()
    val isPhoneValid = phoneNumber.length == 13

    val isFormValid = isUsernameValid && isPasswordValid && isConfirmPasswordValid && isPhoneValid &&
            !usernameTaken && !emailTaken && !phoneTaken

    val isLoading = state is SendOtpUiState.Loading
    val didInit = rememberSaveable { mutableStateOf(false) }

    // Load saved temp user
    LaunchedEffect(Unit) {
        if (!didInit.value) {
            viewModel.getTempUser()?.let { user ->
                username = user.username
                email = user.email ?: ""
                phoneNumber = user.phoneNo
            } ?: run {
                viewModel.resetState()
                username = ""
                password = ""
                confirmPassword = ""
                email = ""
                phoneNumber = "+91"
            }
            didInit.value = true
        }
    }

    // Navigate on success
    LaunchedEffect(state) {
        if (state is SendOtpUiState.Success) {
            val success = state as SendOtpUiState.Success
            navController.navigate("verify_otp/${success.phoneNumber}/${success.otp}") {
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Your Account",
                        fontSize = 22.sp,
                        color = Color(0xFF6A1B9A) // Fixed purple
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White // Always light mode
                )
            )
        },
        containerColor = Color.White // Scaffold background fixed to white
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Username
            InputField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.checkUsername(it)
                },
                label = "Username",
                isError = (!isUsernameValid && username.isNotEmpty()) || usernameTaken,
                supportingText = when {
                    !isUsernameValid && username.isNotEmpty() -> "Username is required"
                    usernameTaken -> "Username already exists"
                    else -> null
                }
            )

            // Password
            InputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                keyboardType = KeyboardType.Password,
                isError = password.isNotEmpty() && !isPasswordValid,
                supportingText = passwordWarning(password).takeIf { it.isNotEmpty() }
            )

            // Confirm Password
            InputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                keyboardType = KeyboardType.Password,
                isError = confirmPassword.isNotEmpty() && !isConfirmPasswordValid,
                supportingText = if (confirmPassword.isNotEmpty() && !isConfirmPasswordValid)
                    "Passwords do not match" else null
            )

            // Email
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.checkEmail(it)
                },
                label = "Email (optional)",
                keyboardType = KeyboardType.Email,
                isError = emailTaken,
                supportingText = if (emailTaken) "Email is already in use" else null
            )

            // Phone
            InputField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 13) {
                        phoneNumber = it
                        viewModel.checkPhone(it)
                    }
                },
                label = "+91XXXXXXXXXX",
                keyboardType = KeyboardType.Phone,
                isError = phoneNumber.isNotEmpty() && (!isPhoneValid || phoneTaken),
                supportingText = when {
                    phoneNumber.isNotEmpty() && !isPhoneValid -> "Enter a valid phone number"
                    isPhoneValid && phoneTaken -> "Phone number already in use"
                    else -> null
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(
                onClick = {
                    viewModel.sendOtp(username, password, phoneNumber, email.takeIf { it.isNotBlank() })
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Next", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            if (state is SendOtpUiState.Error) {
                val error = state as SendOtpUiState.Error
                Text(
                    text = error.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Color.Black) },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6A1B9A),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF6A1B9A),
                containerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}




