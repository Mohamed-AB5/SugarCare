package com.sugarcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.sugercare1.navigation.SugarCareNavHost
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SugarCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // status bar visible with time/icons
        setContent {
            val darkThemeState = remember { mutableStateOf(false) }
            CompositionLocalProvider(LocalDarkTheme provides darkThemeState) {
                SugarCareTheme(darkTheme = darkThemeState.value) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        SugarCareNavHost()
                    }
                }
            }
        }
    }
}
