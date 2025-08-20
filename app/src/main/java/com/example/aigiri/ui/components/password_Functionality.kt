package com.example.aigiri.ui.components

//import org.mindrot.jbcrypt.BCrypt


fun isValidPassword(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { "!@#\$%^&*()_+=-{}[]|:;\"'<>,.?/".contains(it) }
}

fun passwordWarning(password: String): String {
    if (password.isNotBlank() && !isValidPassword(password)) {

        if (!password.any { it.isUpperCase() }) {
            return "At least one uppercase letter"
        }
        if (!password.any { it.isLowerCase() }) {
            return "At least one lowercase letter"
        }
        if (!password.any { it.isDigit() }) {
            return "At least one digit"
        }
        if (!password.any { "!@#\$%^&*()_+=-{}[]|:;\"'<>,.?/".contains(it) }) {
            return "At least one special character"
        }
        if (password.length < 8) {
            return "Password must be at least 8 characters"
        }
    }
    return ""
}


