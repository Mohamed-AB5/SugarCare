package com.sugarcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.sugercare.navigation.SugarCareNavHost
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.ChatViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SugarCareTheme

class MainActivity : ComponentActivity() {
    private val authViewModel   : AuthViewModel    by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val chatViewModel   : ChatViewModel    by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // ✅ Dark mode state — provided to ALL screens
            val darkState = remember { mutableStateOf(false) }

            CompositionLocalProvider(
                LocalDarkTheme provides darkState   // ← هذا هو الحل
            ) {
                SugarCareTheme(darkTheme = darkState.value) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        SugarCareNavHost(
                            authViewModel    = authViewModel,
                            profileViewModel = profileViewModel,
                            chatViewModel    = chatViewModel
                        )
                    }
                }
            }
        }
    }
}