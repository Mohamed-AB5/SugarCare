package com.sugarcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.sugercare1.navigation.SugarCareNavHost 
import com.sugarcare.app.ui.theme.SugarCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        setContent {
       
            val darkThemeState = remember { mutableStateOf(false) }
            
            SugarCareTheme(darkTheme = darkThemeState.value) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    
                    SugarCareNavHost(
                        isDark = darkThemeState.value,
                        onToggleTheme = { darkThemeState.value = !darkThemeState.value }
                    )
                }
            }
            
        }
    }
    private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel("sugar_care_channel", "SugarCare Reminders", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
}

