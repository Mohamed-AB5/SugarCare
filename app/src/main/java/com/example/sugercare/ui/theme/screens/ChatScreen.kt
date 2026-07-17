package com.example.sugercare.ui.theme.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sugercare.chatBot.MessageModel
import com.example.sugercare.viewModels.ChatViewModel
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.GreenAccent2
import com.sugarcare.app.ui.theme.OrangeDrop
import com.sugarcare.app.ui.theme.OrangeDrop2
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.GreenAccent2
import com.sugarcare.app.ui.theme.OrangeDrop2
import com.sugarcare.app.ui.theme.LocalDarkTheme
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatScreen(chatViewModel: ChatViewModel) {
    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "AI Sugar Chat 🤖",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 8.dp),
                color = if (LocalDarkTheme.current.value) Color.White else TealPrimary, fontWeight = FontWeight.Bold
            )
            MessageList(modifier = Modifier.weight(1f), messageList = chatViewModel.messageList.toList())
            MessageInput(chatViewModel, onMessageSend = { chatViewModel.sendMessage(it) })
        }
    }
}

@Composable
fun MessageList(modifier: Modifier, messageList: List<MessageModel>) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true
    ) {
        items(messageList.reversed()) {
            MessageField(messageModel = it)
        }
    }
}

@Composable
fun MessageInput(
    chatViewModel: ChatViewModel,
    onMessageSend: (String) -> Unit
) {
    val message = chatViewModel.message.collectAsState()

    Row(
        modifier = Modifier.padding(vertical = 50.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message.value,
            onValueChange = { chatViewModel.updateMessage(it) },
            label = { Text("Type a message") },
            shape = RoundedCornerShape(14.dp),
            colors = chatFieldColors(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onMessageSend(message.value)
                        chatViewModel.updateMessage("")
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = TealPrimary
                    )
                }
            }
        )
    }

}

@Composable
fun MessageField(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(modifier = Modifier.fillMaxWidth()) {
            val brush = if (isModel) {
                Brush.horizontalGradient(
                    colors = listOf(
                        GreenAccent,
                        GreenAccent2
                    )
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(
                        OrangeDrop,
                        OrangeDrop2
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp
                    )
                    .clip(RoundedCornerShape(27.dp))
                    .background(brush = brush)
                    .padding(16.dp)

            )
            {
                SelectionContainer {
                    Text(text = messageModel.message, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

    }
}


@Composable
private fun chatFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TealPrimary,
    unfocusedBorderColor = if (LocalDarkTheme.current.value) Color.LightGray else Color.Gray,
    focusedLabelColor = TealPrimary,
    unfocusedLabelColor = if (LocalDarkTheme.current.value) Color.LightGray else Color.Gray,
    focusedTextColor = if (LocalDarkTheme.current.value) Color.White else Color.Black,
    unfocusedTextColor = if (LocalDarkTheme.current.value) Color.White else Color.Black,
    cursorColor = TealPrimary
)