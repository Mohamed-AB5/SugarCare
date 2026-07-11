package com.example.sugercare.ui.theme.screens

import android.content.Context
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.viewModels.CounterViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.ProfilePicture   // ← now exists in SharedComponents
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.*
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.SurfaceDark
import android.R.attr.textColor
import com.sugarcare.app.ui.theme.GreenAccent3
import com.sugarcare.app.ui.theme.White

/**
 * Home Screen – main dashboard with Glucose Logs, Meal Plan,
 * Weekly Analytics, and Medication Plan quick tiles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    counterViewModel: CounterViewModel
) {
    var insulinEnabled by remember { mutableStateOf(true) }
    var metforminEnabled by remember { mutableStateOf(true) }
    var notifEnabled by remember { mutableStateOf(false) }
    val state = counterViewModel.uiState.collectAsState() // for fire streak icon (counter)

    LaunchedEffect(Unit) { profileViewModel.loadProfile() }

    val isDark = LocalDarkTheme.current.value
    val bgColor = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor = if (isDark) Color(0xFF80CBC4) else TextMedium
    val navColor = if (isDark) SurfaceDark else Color.White
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    SugarCareBackground {
        Scaffold(
            containerColor = Color.Transparent,

            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Sugar Care",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFE0F2F1) else TealDark
                        )
                    },

                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                            ProfilePicture(profileViewModel, fontSize = 14.sp)
                        }
                    },

                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = OrangeDrop) {
                                        Text("3", fontSize = 9.sp)
                                    }
                                }) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = "Notifications",
                                    tint = OrangeDrop
                                )
                            }
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = bgColor
                    )
                )
            },


            bottomBar = {
                NavigationBar(containerColor = navColor, tonalElevation = 8.dp) {
                    listOf(
                        Triple("Home", Icons.Filled.Home, Screen.Home.route),
                        Triple("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                        Triple("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
                        Triple("Profile", Icons.Filled.Person, Screen.Profile.route)
                    ).forEach { (label, icon, route) ->
                        NavigationBarItem(
                            selected = route == Screen.Home.route,
                            onClick = {
                                if (route != Screen.Home.route) navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                unselectedIconColor = navText,
                                indicatorColor = TealLight
                            )
                        )
                    }
                }
            }) { paddingValues ->

            // ── Dashboard grid ────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)

                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Left : Column 1: Glucose Logs | Weekly Analytics  |  Chat Bot  ▬▬▬▬
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ──── Glucose Logs mini card  ───────
                        DashboardCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "Glucose Logs",
                            subtitle = "Last: 120 mg/dL",
                            icon = Icons.Filled.Favorite,
                            iconTint = OrangeDrop,
                            buttonText = "Log New Reading",
                            cardColor = cardColor,
                            subColor = subColor,
                            titleColor = textColor
                        ) { navController.navigate(Screen.Logs.route) }

                        // ──── Weekly analytics mini card ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Weekly Analytics",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Spacer(Modifier.height(8.dp))
                                Icon(
                                    Icons.Filled.Vaccines,
                                    null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Average: 12.20", fontSize = 12.sp, color = subColor)
                                Spacer(Modifier.height(8.dp))
                                GradientButton("Analyze Trends") { navController.navigate(Screen.WeeklyAnalytics.route) }
                            }
                        }

                        // ──── ChatBot mini card ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "AI Sugar Chat 🤖",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Spacer(Modifier.height(8.dp))

                                Icon(
                                    painterResource(com.sugarcare.app.R.drawable.ic_chat),
                                    null,
                                    tint = OrangeDrop,
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(Modifier.height(8.dp))
                                GradientButton("Get AI Advice!") { navController.navigate(Screen.ChatScreen.route) }
                            }
                        }
                    }

                    // Right : Column 2: Meal Plan | Medication Plan | Sugar Counter ▬▬▬▬
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ──── Meal Plan mini card ────────
                        DashboardCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "My Meal Plan",
                            subtitle = "",
                            icon = Icons.Filled.Restaurant,
                            iconTint = GreenAccent,
                            buttonText = "Meal Suggestions",
                            cardColor = cardColor,
                            subColor = subColor,
                            titleColor = textColor
                        ) { navController.navigate(Screen.MealPlan.route) }

                        // Medication Plan mini card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Medication Plan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Spacer(Modifier.height(8.dp))
                                MedToggleRow("Insulin", insulinEnabled, textColor) {
                                    insulinEnabled = it
                                }
                                MedToggleRow("Metformin", metforminEnabled, textColor) {
                                    metforminEnabled = it
                                }
                                MedToggleRow("Notifications", notifEnabled, textColor) {
                                    notifEnabled = it
                                }
                                Spacer(Modifier.height(8.dp))
                                GradientButton("Set Notifications") { navController.navigate(Screen.Medications.route) }
                            }
                        }

                        // ──── Sugar Counter mini card ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No Sugar Challenge ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = textColor
                                )
                                Spacer(Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        modifier = Modifier
                                            .clickable { navController.navigate(Screen.CounterScreen.route) }
                                            .size(40.dp),
                                        imageVector = Icons.Filled.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = FireIcon,
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        modifier = Modifier.clickable {
                                            navController.navigate(
                                                Screen.CounterScreen.route
                                            )
                                        },
                                        text = "${state.value.bestStreak} days",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FireIcon
                                    )
                                }
                                Spacer(Modifier.height(8.dp))

                            }
                        }

                    }
                }

                // ──── Emergency Contact────────
                Card(
                    modifier = Modifier.fillMaxHeight(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    onClick = { navController.navigate(Screen.EmergencyContact.route) }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Emergency, null,
                                tint = Color(0xFFE53935), modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Emergency Contacts", fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, color = Color(0xFFE53935)
                            )
                            Text(
                                "Tap to manage emergency contacts",
                                fontSize = 11.sp,
                                color = Color(0xFFE53935).copy(alpha = 0.7f)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Filled.ChevronRight, null,
                            tint = Color(0xFFE53935).copy(alpha = 0.7f)
                        )
                    }
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
            .padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
        Text(text, fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
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
    cardColor: Color,
    subColor: Color,
    titleColor: Color,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
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
private fun MedToggleRow(
    label: String, checked: Boolean, textColor: Color, onToggle: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = textColor)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}
