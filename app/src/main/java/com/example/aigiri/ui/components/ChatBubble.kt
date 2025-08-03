package com.example.aigiri.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigiri.model.ChatMessage

private val UserBubbleColor = Color(0xFF7B2CBF)         // Deep Purple
private val BotBubbleColor = Color(0xFFF3E8FF)          // Soft Lavender
private val UserTextColor = Color.White
private val BotTextColor = Color.Black

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "User"
    val bubbleColor = if (isUser) UserBubbleColor else BotBubbleColor
    val textColor = if (isUser) UserTextColor else BotTextColor
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val arrangement = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = arrangement
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = if (isUser) 16.dp else 0.dp,
                topEnd = if (isUser) 0.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            tonalElevation = 2.dp,
            modifier = Modifier
                .widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = message.message,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.time,
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
