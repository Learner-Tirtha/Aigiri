//package com.example.aigiri.viewmodel
//
//
//import androidx.compose.runtime.*
//import androidx.lifecycle.ViewModel
//import com.example.aigiri.repository.UserRepository
//import com.example.aigiri.ui.components.isSamePassword
//
//class ForgotPasswordViewModel(private val userRepository: UserRepository) : ViewModel(
//) {
//
//
//    private val _newPassword = mutableStateOf("")
//    val newPassword: State<String> = _newPassword
//
//    private val _confirmPassword = mutableStateOf("")
//    val confirmPassword: State<String> = _confirmPassword
//
//    private val oldHashedPassword= userRepository.fetchPasswordByUsername("test")
//    var showSuccessMessage by mutableStateOf(false)
//        private set
//
//
//
//    fun onNewPasswordChange(value: String) {
//        _newPassword.value = value
//    }
//
//    fun onConfirmPasswordChange(value: String) {
//        _confirmPassword.value = value
//    }
//
//    fun sendReset() {
//        // Simulate password reset success
//        showSuccessMessage = true
//    }
//
//    fun onSuccessMessageShown() {
//        showSuccessMessage = false
//    }
//    fun isSameAsOldPassword(): Boolean {
//        return isSamePassword(oldHashedPassword, newPassword.value)
//    }
//
//
//}
