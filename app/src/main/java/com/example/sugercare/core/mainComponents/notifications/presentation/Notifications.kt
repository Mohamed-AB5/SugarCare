package com.example.sugercare.core.mainComponents.notifications.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary

// ══════════════════════════════════════════════════════════════
//  1. NOTIFICATIONS
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notifications",
                        fontWeight = FontWeight.Bold,
                        color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(110.dp).clip(CircleShape).background(TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.NotificationsNone, null,
                        tint = TealPrimary, modifier = Modifier.size(56.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text("No notifications yet",
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
                Spacer(Modifier.height(8.dp))
                Text(
                    "You're all caught up!\nWe'll notify you when something new arrives.",
                    fontSize = 14.sp,
                    color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(0xFF4A6565),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

