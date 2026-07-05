package com.sugarcare.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var insulinEnabled   by remember { mutableStateOf(true) }
    var metforminEnabled by remember { mutableStateOf(true) }
    var notifEnabled     by remember { mutableStateOf(false) }

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = { Text("Sugar Care", fontWeight = FontWeight.Bold, color = TealDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(TealLight),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.Person, null, tint = TealPrimary) }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        BadgedBox(badge = {
                            Badge(containerColor = OrangeDrop) { Text("3", fontSize = 9.sp) }
                        }) {
                            Icon(Icons.Filled.Notifications, null, tint = OrangeDrop)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f), title = "Glucose Logs",
                        subtitle = "Last: 120 mg/dL", icon = Icons.Filled.Favorite,
                        iconTint = OrangeDrop, buttonText = "Log New Reading"
                    ) { navController.navigate(Screen.Logs.route) }

                    DashboardCard(
                        modifier = Modifier.weight(1f), title = "My Meal Plan",
                        subtitle = "", icon = Icons.Filled.Restaurant,
                        iconTint = GreenAccent, buttonText = "Meal Suggestions"
                    ) { navController.navigate(Screen.MealPlan.route) }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Weekly Analytics", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(8.dp))
                            Icon(Icons.Filled.BarChart, null, tint = TealPrimary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Average: 12.20", fontSize = 12.sp, color = TextMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { navController.navigate(Screen.WeeklyAnalytics.route) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                                elevation = ButtonDefaults.buttonElevation(0.dp),
                                contentPadding = PaddingValues(4.dp)
                            ) { Text("Analyze Trends", fontSize = 11.sp) }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Medication Plan", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(8.dp))
                            MedToggleRow("Insulin",       insulinEnabled)   { insulinEnabled   = it }
                            MedToggleRow("Metformin",     metforminEnabled) { metforminEnabled = it }
                            MedToggleRow("Notifications", notifEnabled)     { notifEnabled     = it }
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { navController.navigate(Screen.Medications.route) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                                elevation = ButtonDefaults.buttonElevation(0.dp),
                                contentPadding = PaddingValues(4.dp)
                            ) { Text("Set Notifications", fontSize = 11.sp) }
                        }
                    }
                }
            }

            BottomNavBar(navController, Screen.Home.route)
        }
    }
}


@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick  = { if (currentRoute != Screen.Home.route) navController.navigate(Screen.Home.route) { launchSingleTop = true } },
            icon     = { Icon(Icons.Filled.Home, "Home") },
            label    = { Text("Home", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, unselectedIconColor = TextMedium, indicatorColor = TealLight)
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Logs.route,
            onClick  = { if (currentRoute != Screen.Logs.route) navController.navigate(Screen.Logs.route) { launchSingleTop = true } },
            icon     = { Icon(Icons.AutoMirrored.Filled.Assignment, "Logs") },
            label    = { Text("Logs", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, unselectedIconColor = TextMedium, indicatorColor = TealLight)
        )
        NavigationBarItem(
            selected = currentRoute == Screen.MealPlan.route,
            onClick  = { if (currentRoute != Screen.MealPlan.route) navController.navigate(Screen.MealPlan.route) { launchSingleTop = true } },
            icon     = { Icon(Icons.Filled.Restaurant, "Meals") },
            label    = { Text("Meals", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, unselectedIconColor = TextMedium, indicatorColor = TealLight)
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick  = { if (currentRoute != Screen.Profile.route) navController.navigate(Screen.Profile.route) { launchSingleTop = true } },
            icon     = { Icon(Icons.Filled.Person, "Profile") },
            label    = { Text("Profile", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, unselectedIconColor = TextMedium, indicatorColor = TealLight)
        )
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier, title: String, subtitle: String,
    icon: ImageVector, iconTint: Color, buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 11.sp, color = TextMedium)
            }
            Spacer(Modifier.height(8.dp))
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(44.dp))
            Spacer(Modifier.height(8.dp))
            Button(onClick = onButtonClick, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                contentPadding = PaddingValues(4.dp)
            ) { Text(buttonText, fontSize = 11.sp) }
        }
    }
}

@Composable
private fun MedToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary, checkedTrackColor = TealLight))
    }
}

