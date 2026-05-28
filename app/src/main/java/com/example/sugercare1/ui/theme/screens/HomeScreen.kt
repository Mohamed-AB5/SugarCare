package com.example.sugercare1.ui.theme.screens

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.screens.SignInScreen
import com.sugarcare.app.ui.theme.*

/**
 * Home Screen – main dashboard with Glucose Logs, Meal Plan,
 * Weekly Analytics, and Medication Plan quick tiles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var insulinEnabled  by remember { mutableStateOf(true) }
    var metforminEnabled by remember { mutableStateOf(true) }
    var notifEnabled    by remember { mutableStateOf(false) }

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── App Bar ───────────────────────────────────────
            TopAppBar(
                title = {
                    Text(
                        "Sugar Care",
                        fontWeight = FontWeight.Bold,
                        color      = TealDark
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, null, tint = TealPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Notifications, null, tint = OrangeDrop)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )

            // ── Dashboard grid ────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Glucose Logs | Meal Plan
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardCard(
                        modifier       = Modifier.weight(1f),
                        title          = "Glucose Logs",
                        subtitle       = "Last: 120 mg/dL",
                        icon           = Icons.Filled.Favorite,
                        iconTint       = OrangeDrop,
                        buttonText     = "Log New Reading",
                        onButtonClick  = { /* navigate to logs */ }
                    )
                    DashboardCard(
                        modifier      = Modifier.weight(1f),
                        title         = "My Meal Plan",
                        subtitle      = "",
                        icon          = Icons.Filled.Restaurant,
                        iconTint      = GreenAccent,
                        buttonText    = "Meal Suggestions",
                        onButtonClick = { navController.navigate(Screen.Home.route) } // Corrected destination
                    )
                }

                // Row 2: Weekly Analytics | Medication Plan
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Weekly analytics mini card
                    Card(
                        modifier  = Modifier.weight(1f),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Weekly Analytics",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp,
                                color      = TextDark
                            )
                            Spacer(Modifier.height(8.dp))
                            Icon(
                                Icons.Filled.Vaccines,
                                null,
                                tint     = TealPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Average: 12.20",
                                fontSize = 12.sp,
                                color    = TextMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick  = { navController.navigate(Screen.Home.route) }, // Corrected destination
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(20.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text("Analyze Trends", fontSize = 11.sp)
                            }
                        }
                    }

                    // Medication Plan mini card
                    Card(
                        modifier  = Modifier.weight(1f),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Medication Plan",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp,
                                color      = TextDark
                            )
                            Spacer(Modifier.height(8.dp))
                            MedToggleRow("Insulin",   insulinEnabled)  { insulinEnabled  = it }
                            MedToggleRow("Metformin", metforminEnabled) { metforminEnabled = it }
                            MedToggleRow("Activate\nNotifications", notifEnabled) { notifEnabled = it }
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick  = { navController.navigate(Screen.Home.route) }, // Corrected destination
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(20.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text("Set Notifications", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // ── Bottom Nav ────────────────────────────────────
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                listOf(
                    Triple("Home",    Icons.Filled.Home,    Screen.Home.route),
                    Triple("Logs",    Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route), // Corrected route
                    Triple("Meals",   Icons.Filled.Restaurant, Screen.MealPlan.route), // Corrected route
                    Triple("Profile", Icons.Filled.Person,  Screen.Profile.route) // Corrected route
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == Screen.Home.route,
                        onClick  = { navController.navigate(route) },
                        icon     = { Icon(icon, null) },
                        label    = { Text(label, fontSize = 11.sp) },
                        colors   = NavigationBarItemDefaults.colors(
                            selectedIconColor   = TealPrimary,
                            indicatorColor      = TealLight
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 11.sp, color = TextMedium)
            }
            Spacer(Modifier.height(8.dp))
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(44.dp))
            Spacer(Modifier.height(8.dp))
            Button(
                onClick  = onButtonClick,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(20.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(buttonText, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun MedToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextDark)
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            modifier        = Modifier.scale(0.7f),
            colors          = SwitchDefaults.colors(checkedThumbColor = TealPrimary, checkedTrackColor = TealLight)
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    SugarCareTheme {
        HomeScreen(navController = rememberNavController())
    }
}