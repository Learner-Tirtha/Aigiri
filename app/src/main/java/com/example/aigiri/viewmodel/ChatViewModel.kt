package com.example.aigiri.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.model.ChatMessage
import com.example.aigiri.model.ChatRequest
import com.example.aigiri.repository.ChatRepository
import com.example.aigiri.ui.components.ChatPredefinedAnswers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    val predefinedQuestions = ChatPredefinedAnswers.questions

    fun sendPredefinedMessage(question: String) {
        val userMsg = ChatMessage("User", question, getCurrentTime())
        val answer = ChatPredefinedAnswers.answers[question] ?: "Sorry, no information available."
        val botMsg = ChatMessage("Legal Assistant", answer, getCurrentTime())
        _messages.value = _messages.value + listOf(userMsg, botMsg)
    }

    fun sendMessageToApi(message: String) {
        val userMsg = ChatMessage("User", message, getCurrentTime())
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            try {
                val response = repository.getChatResponse(ChatRequest(question = message))
                val answer = if (response.isSuccessful) {
                    response.body()?.answer ?: "Sorry, no answer received."
                } else {
                    "Error: ${response.code()}"
                }
                val botMsg = ChatMessage("Legal Assistant", answer, getCurrentTime())
                _messages.value = _messages.value + botMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage("Legal Assistant", "Error: ${e.localizedMessage}", getCurrentTime())
                _messages.value = _messages.value + errorMsg
            }
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
