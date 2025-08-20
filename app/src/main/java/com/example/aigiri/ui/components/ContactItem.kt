package com.example.aigiri.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigiri.model.EmergencyContact
import kotlin.math.roundToInt


@Composable
fun ContactItem(
    contact: EmergencyContact,
    onDelete: () -> Unit,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
    dragHandle: Modifier = Modifier
) {
    // Shake effect when NOT dragging (example)
    val shouldShake = !isDragging
    val shakeTarget by rememberUpdatedState(if (shouldShake) 4f else 0f)

    val shakeAnimation = rememberInfiniteTransition(label = "shakeAnimation")
    val shakeOffset by shakeAnimation.animateFloat(
        initialValue = 0f,
        targetValue = shakeTarget,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(500)
        ),
        label = "shakeOffset"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) Color.White.copy(alpha = 0.9f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Commenting out drag handle for now
                /*
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Drag Handle",
                    tint = Color.Gray,
                    modifier = dragHandle
                        .size(24.dp)
                        .offset { IntOffset(shakeOffset.roundToInt(), 0) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                */
                // Just put spacer in place of drag handle so layout stays consistent
                Spacer(modifier = Modifier.width(32.dp))

                Column {
                    Text(
                        text = "Priority ${contact.priority}: ${contact.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isDragging) 20.sp else 16.sp
                    )
                    Text(
                        text = contact.phoneNumber,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}
