package com.example.sugercare1.ui.theme.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue   = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
        delay(2000L)
        navController.navigate(Screen.Welcome.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F7A7A),
                        Color(0xFF2E9B9B),
                        Color(0xFFB2DFDB)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.scale(scale.value)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text("Sugar", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Care",  fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB2DFDB))
            Spacer(Modifier.height(8.dp))
            Text(
                "Your Path to Healthy Living",
                fontSize = 14.sp,
                color    = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
