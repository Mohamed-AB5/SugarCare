package com.sugarcare.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
      
        scale.animateTo(1f, animationSpec = tween(700, easing = EaseOutBack))
        alpha.animateTo(1f, animationSpec = tween(500))
        delay(1200)
      
        navController.navigate(Screen.Welcome.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value).alpha(alpha.value)
        ) {
           
            Text(
                text      = "▲",
                fontSize  = 80.sp,
                color     = Color.Black,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text       = "Sugar",
                color      = TealPrimary,
                fontSize   = 44.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 48.sp
            )
            Text(
                text       = "Care",
                color      = GreenAccent,
                fontSize   = 44.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 48.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Your diabetes companion",
                color    = TextMedium,
                fontSize = 15.sp
            )
        }
    }
}
