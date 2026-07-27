package com.example.sugercare.core.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sugercare.core.features.counter.presentation.CounterViewModel
import com.example.sugercare.core.features.profile.presentation.ProfileViewModel
import com.sugarcare.app.R
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.GradientButton
import com.sugarcare.app.ui.components.ProfilePicture
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.FireIcon
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.GreenAccent3
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.OrangeDrop
import com.sugarcare.app.ui.theme.SurfaceDark
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextMedium

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

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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
                        IconButton(onClick = { navController.navigate(Screen.Medications.route) }) {
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
                            selected = route == currentRoute,
                            onClick = {
                                if (route != Screen.Home.route) navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp, color = textColor) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                unselectedIconColor = navText,
                                indicatorColor = TealLight,
                                selectedTextColor = navText,
                                unselectedTextColor = textColor
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
                            elevation = CardDefaults.cardElevation(4.dp)
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
                                GradientButton(
                                    "Analyze Trends",
                                    onClick = { navController.navigate(Screen.WeeklyAnalytics.route) },
                                    color1 = GreenAccent,
                                    color2 = GreenAccent3
                                ){}
                            }
                        }

                        // ──── ChatBot mini card ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(4.dp)
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
                                    painterResource(R.drawable.ic_chat),
                                    null,
                                    tint = OrangeDrop,
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(Modifier.height(8.dp))
                                GradientButton(
                                    "Get AI Advice!",
                                    onClick = { navController.navigate(Screen.ChatScreen.route) },
                                    color1 = GreenAccent,
                                    color2 = GreenAccent3
                                ){}
                            }
                        }
                    }

                    // Right : Column 2: Meal Plan | Medication Plan | Sugar Counter | Emergency Contact ▬▬▬▬
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
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

                        // ──── Medication Plan mini card ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "Medication Plan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textColor
                                )
                                Spacer(Modifier.height(4.dp))
                                MedToggleRow("Insulin", insulinEnabled, textColor) {
                                    insulinEnabled = it
                                }
                                MedToggleRow("Metformin", metforminEnabled, textColor) {
                                    metforminEnabled = it
                                }
                                MedToggleRow("Notifications", notifEnabled, textColor) {
                                    notifEnabled = it
                                }
                                Spacer(Modifier.height(4.dp))
                                GradientButton(
                                    text = "Set Notifications",
                                    onClick = { navController.navigate(Screen.Medications.route) },
                                    color1 = GreenAccent,
                                    color2 = GreenAccent3
                                ){}
                            }
                        }

                        // ──── Sugar Counter mini card ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(4.dp)
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

                        // ──── Emergency Contact mini card  ────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { navController.navigate(Screen.EmergencyContact.route) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
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
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Emergency Contacts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFE53935),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "Tap to manage emergency contacts",
                                    fontSize = 10.sp,
                                    color = Color(0xFFE53935).copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

// (private fun GradientButton) deleted to use the one @SharedCompnents

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
        elevation = CardDefaults.cardElevation(4.dp)
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
            GradientButton(
                text = buttonText,
                textSize =10.sp ,
                onClick = { onButtonClick()},
                modifier = Modifier
                    .width(130.dp)
                    .align(Alignment.CenterHorizontally),
                color1= GreenAccent,
                color2= GreenAccent3
            ){}
        }
    }
}

@Composable
private fun MedToggleRow(
    label: String, checked: Boolean, textColor: Color, onToggle: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 11.sp, color = textColor)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(0.8f)
        )
    }
}