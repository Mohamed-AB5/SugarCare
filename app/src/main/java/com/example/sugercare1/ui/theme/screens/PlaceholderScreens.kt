package com.sugarcare.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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

// ── Profile Screen ────────────────────────────────────────────
@Composable
fun ProfileScreen(navController: NavHostController) {
    PlaceholderScreen(
        title          = "Profile",
        navController  = navController,
        currentRoute   = Screen.Profile.route
    )
}

// ── Glucose Logs Screen ───────────────────────────────────────
@Composable
fun LogsScreen(navController: NavHostController) {
    PlaceholderScreen(
        title          = "Glucose Logs",
        navController  = navController,
        currentRoute   = Screen.Logs.route
    )
}

// ── Shared placeholder for screens not fully designed ─────────
@Composable
private fun PlaceholderScreen(
    title: String,
    navController: NavHostController,
    currentRoute: String
) {
    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Person,
                        null,
                        tint     = TealPrimary,
                        modifier = Modifier.size(80.dp)
                    )
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

            // ── Bottom Nav shared ─────────────────────────────
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home",    Icons.Filled.Home,      Screen.Home.route),
                    Triple("Meds",    Icons.Filled.Medication, Screen.Medications.route),
                    Triple("Trends",  Icons.Filled.BarChart,  Screen.WeeklyAnalytics.route),
                    Triple("Profile", Icons.Filled.Person,    Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == currentRoute,
                        onClick  = { navController.navigate(route) },
                        icon     = { Icon(icon, null) },
                        label    = { Text(label, fontSize = 11.sp) },
                        colors   = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            indicatorColor    = TealLight
                        )
                    )
                }
            }
        }
    }
}
