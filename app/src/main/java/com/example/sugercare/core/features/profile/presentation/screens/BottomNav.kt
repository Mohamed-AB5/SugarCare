package com.example.sugercare.core.features.profile.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextMedium

// ── Shared bottom nav ─────────────────────────────────────────
//  Mohamed : it was private
//  Mohamed : it was in NewScreens
@Composable
fun BottomNav(navController: NavHostController, currentRoute: String) {
    val isDark = LocalDarkTheme.current.value
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        listOf(
            Triple("Home",    Icons.Filled.Home,       Screen.Home.route),
            Triple("Logs",    Icons.Filled.Assignment,   Screen.Logs.route),
            Triple("Meals",   Icons.Filled.Restaurant, Screen.MealPlan.route),
            Triple("Profile", Icons.Filled.Person,     Screen.Profile.route)
        ).forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = {
                    if (currentRoute != route)
                        navController.navigate(route) { launchSingleTop = true }
                },
                icon   = { Icon(icon, contentDescription = label) },
                label  = { Text(label, fontSize = 11.sp, color = textColor) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = TealPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    indicatorColor      = TealLight,
                )
            )
        }
    }
}