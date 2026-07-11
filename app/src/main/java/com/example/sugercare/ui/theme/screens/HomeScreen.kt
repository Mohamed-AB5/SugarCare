
package com.example.sugercare.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat   // ← AutoMirrored fixes deprecation
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.viewModels.ProfileViewModel
import com.example.sugercare.navigation.Screen
import com.sugarcare.app.ui.components.ProfilePicture   // ← now exists in SharedComponents
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.*
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.SurfaceDark
import android.R.attr.textColor
import com.example.sugarcare.app.ui.theme.GreenAccent3
import com.example.sugarcare.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, profileViewModel: ProfileViewModel) {
    var insulinEnabled   by remember { mutableStateOf(true) }
    var metforminEnabled by remember { mutableStateOf(true) }
    var notifEnabled     by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { profileViewModel.loadProfile() }

    val isDark    = LocalDarkTheme.current.value
    val bgColor   = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark    else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor  = if (isDark) Color(0xFF80CBC4)  else TextMedium
    val navColor  = if (isDark) SurfaceDark    else Color.White
    val navText   = if (isDark) Color(0xFF80CBC4)  else TextMedium

    SugarCareBackground {
        Column(Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────
            TopAppBar(
                title = { Text("Sugar Care", fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFE0F2F1) else TealDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        ProfilePicture(profileViewModel, fontSize = 14.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        BadgedBox(badge = {
                            Badge(containerColor = OrangeDrop) { Text("3", fontSize = 9.sp) }
                        }) {
                            Icon(Icons.Filled.Notifications, "Notifications", tint = OrangeDrop)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )

            // ── Dashboard ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Glucose Logs | Meal Plan
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Glucose Logs",
                        subtitle = "Last: 120 mg/dL",
                        icon = Icons.Filled.Favorite,
                        iconTint = OrangeDrop,
                        buttonText = "Log New Reading",
                        cardColor = cardColor,
                        subColor = subColor,
                        titleColor = textColor
                    ) { navController.navigate(Screen.Logs.route) }

                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "My Meal Plan",
                        subtitle = "",
                        icon = Icons.Filled.Restaurant,
                        iconTint = GreenAccent,
                        buttonText = "Meal Suggestions",
                        cardColor = cardColor,
                        subColor = subColor,
                        titleColor = textColor
                    ) { navController.navigate(Screen.MealPlan.route) }
                }

                // Row 2: Weekly Analytics | Medication Plan
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Weekly Analytics", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                            Spacer(Modifier.height(8.dp))
                            Icon(Icons.Filled.Vaccines, null, tint = TealPrimary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Average: 12.20", fontSize = 12.sp, color = subColor)
                            Spacer(Modifier.height(8.dp))
                            GradientButton("Analyze Trends") { navController.navigate(Screen.WeeklyAnalytics.route) }
                        }
                    }

                    Card(Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Medication Plan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                            Spacer(Modifier.height(8.dp))
                            MedToggleRow("Insulin", insulinEnabled, textColor)       { insulinEnabled   = it }
                            MedToggleRow("Metformin", metforminEnabled, textColor)   { metforminEnabled = it }
                            MedToggleRow("Notifications", notifEnabled, textColor)   { notifEnabled     = it }
                            Spacer(Modifier.height(8.dp))
                            GradientButton("Set Notifications") { navController.navigate(Screen.Medications.route) }
                        }
                    }
                }

                // Row 3: AI ChatBot
                Row(Modifier.fillMaxWidth()) {
                    Card(Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("AI Sugar Chat 🤖", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                            Spacer(Modifier.height(8.dp))
                            // ← AutoMirrored.Filled.Chat fixes the deprecation warning
                            Icon(Icons.AutoMirrored.Filled.Chat, null,
                                tint = TealPrimary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            GradientButton("Get AI Advice!") { navController.navigate(Screen.ChatScreen.route) }
                        }
                    }
                }

                // Row 4: Emergency Contact
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(20.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    onClick   = { navController.navigate(Screen.EmergencyContact.route) }
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(44.dp).clip(CircleShape)
                                .background(Color(0xFFE53935).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Emergency, null,
                                tint = Color(0xFFE53935), modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Emergency Contacts", fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, color = Color(0xFFE53935))
                            Text("Tap to manage emergency contacts",
                                fontSize = 11.sp, color = Color(0xFFE53935).copy(alpha = 0.7f))
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, null,
                            tint = Color(0xFFE53935).copy(alpha = 0.7f))
                    }
                }
            }

            // ── Bottom Nav ────────────────────────────────────
            NavigationBar(containerColor = navColor, tonalElevation = 0.dp) {
                listOf(
                    Triple("Home",    Icons.Filled.Home,                    Screen.Home.route),
                    Triple("Logs",    Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                    Triple("Meals",   Icons.Filled.Restaurant,              Screen.MealPlan.route),
                    Triple("Profile", Icons.Filled.Person,                  Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == Screen.Home.route,
                        onClick  = {
                            if (route != Screen.Home.route)
                                navController.navigate(route) { launchSingleTop = true }
                        },
                        icon   = { Icon(icon, contentDescription = label) },
                        label  = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = TealPrimary,
                            unselectedIconColor = navText,
                            indicatorColor      = TealLight
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(GreenAccent, GreenAccent3)))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier, title: String, subtitle: String,
    icon: ImageVector, iconTint: Color, buttonText: String,
    cardColor: Color, subColor: Color, titleColor: Color,
    onButtonClick: () -> Unit
) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = titleColor)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 11.sp, color = subColor)
            }
            Spacer(Modifier.height(8.dp))
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(44.dp))
            Spacer(Modifier.height(8.dp))
            GradientButton(buttonText) { onButtonClick() }
        }
    }
}

@Composable
private fun MedToggleRow(label: String, checked: Boolean, textColor: Color, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = textColor)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}