package com.sugarcare.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.*
import kotlin.collections.forEach


@Composable
fun LogsScreen(navController: NavHostController) {
    PlaceholderScreen(
        title         = "Glucose Logs",
        navController = navController,
        currentRoute  = Screen.Logs.route
    )
}

@Composable
fun SugarCareBottomNavBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        listOf(
            Triple("Home", Icons.Filled.Home, Screen.Home.route),
            Triple("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
            Triple("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
            Triple("Profile", Icons.Filled.Person, Screen.Profile.route)
        ).forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = route == currentRoute,
                onClick  = {
                    if (route != currentRoute)
                        navController.navigate(route) { launchSingleTop = true }
                },
                icon  = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = TealPrimary,
                    unselectedIconColor = TextMedium,
                    indicatorColor      = TealLight
                )
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    navController: NavHostController,
    currentRoute: String
) {
    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier         = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.GridView, null, tint = TealPrimary, modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        title,
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TealPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Coming soon…", color = TextMedium, fontSize = 14.sp)
                }
            }
            SugarCareBottomNavBar(navController, currentRoute)
        }
    }
}