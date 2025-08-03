package com.example.aigiri.model

data class User(
    val username: String,
    val phoneNo: String ,
    val password: String,
    val email: String ? = null
)
