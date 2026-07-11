package com.sugarcare.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.navigation.Screen
import com.sugarcare.app.R
import com.sugarcare.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, tween(700, easing = EaseOutBack))
        alpha.animateTo(1f, tween(500))
        delay(1400)
        navController.navigate(Screen.Welcome.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        Modifier.fillMaxSize().background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.scale(scale.value).alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter            = painterResource(id = R.drawable.ic_logo),
                contentDescription = "SugarCare logo",
                modifier           = Modifier.size(160.dp)
            )
            Spacer(Modifier.height(20.dp))
            Row {
                Text("Sugar", color = TealPrimary,   fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Text("·",    color = TealDark,       fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Text("Care", color = TealDark,       fontSize = 38.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Text("Glucose, Meals & Balance", color = TextMedium, fontSize = 14.sp)
        }
    }
}