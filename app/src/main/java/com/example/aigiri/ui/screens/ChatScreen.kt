package com.example.aigiri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aigiri.R
import com.example.aigiri.ui.components.ChatBubble
import com.example.aigiri.viewmodel.ChatViewModel

private val PrimaryPurple = Color(0xFF7B2CBF)
private val LightPurple = Color(0xFFF3E8FF)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    navController: NavController
) {
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.durga),
                    contentDescription = "Back",
                    modifier = Modifier.size(32.dp),
                    tint = PrimaryPurple
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Women Safety Help",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // Chat Messages
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .padding(4.dp),
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            // Inside ChatScreen Composable
            val listState = rememberLazyListState()

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.lastIndex)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = listState,
                reverseLayout = false
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
            }
        }

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Type your question...") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = LightPurple,
                    focusedIndicatorColor = PrimaryPurple,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessageToApi(inputText)
                        inputText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Send", color = Color.White)
            }
        }

        // Predefined Questions
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Quick Help:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PrimaryPurple,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            viewModel.predefinedQuestions.forEach { question ->
                Button(
                    onClick = { viewModel.sendPredefinedMessage(question) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
                ) {
                    Text(text = question, color = PrimaryPurple)
                }
            }
        }
    }
}
