package com.example.sugercare.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.chatBot.Constants
import com.example.sugercare.chatBot.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {


    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-3.1-flash-lite",
        apiKey = Constants.geminiApiKey,
    )

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map { content(it.role) { text(it.message) } }.toList()
                )
                messageList.add(MessageModel(question, "user"))

                val response = chat.sendMessage(question)

                messageList.add(MessageModel(response.text.toString(), "model"))

                Log.i("Response form gemini", response.text.toString())
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
                messageList.add(MessageModel("Error: ${e.localizedMessage}", "model"))
            }
        }

    }
}