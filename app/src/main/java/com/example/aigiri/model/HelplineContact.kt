package com.example.aigiri.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class HelplineContact(
    val number: String,
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color
)