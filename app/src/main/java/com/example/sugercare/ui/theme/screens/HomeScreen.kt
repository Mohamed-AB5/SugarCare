package com.example.sugercare.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.viewModels.CounterViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.ProfilePicture
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.*
import kotlin.collections.forEach
import kotlin.text.isNotEmpty

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

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }
    SugarCareBackground {
        Scaffold(
            containerColor = Color.Transparent,

            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Sugar Care",
                            fontWeight = FontWeight.Bold,
                            color = TealDark
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
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = "Notifications",
                                    tint = OrangeDrop
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundLight
                    )
                )
            },


            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    listOf(
                        Triple("Home", Icons.Filled.Home, Screen.Home.route),
                        Triple("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                        Triple("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
                        Triple("Profile", Icons.Filled.Person, Screen.Profile.route)
                    ).forEach { (label, icon, route) ->
                        NavigationBarItem(
                            selected = route == Screen.Home.route,
                            onClick = {
                                if (route != Screen.Home.route)
                                    navController.navigate(route) { launchSingleTop = true }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                unselectedIconColor = TextMedium,
                                indicatorColor = TealLight
                            )
                        )
                    }
                }
            }
        )
        { paddingValues ->

            // ── Dashboard grid ────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Left : Column 1: Glucose Logs | Chat Bot | Weekly Analytics    ▬▬▬▬
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
                        onButtonClick = { navController.navigate(Screen.Logs.route) }
                    )

                    // ──── Weekly analytics mini card ────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                color = TextDark
                            )
                            Spacer(Modifier.height(8.dp))
                            Icon(
                                Icons.Filled.Vaccines,
                                null,
                                tint = TealPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Average: 12.20", fontSize = 12.sp, color = TextMedium)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(GreenAccent, GreenAccent3)
                                        )
                                    )
                                    .clickable { navController.navigate(Screen.WeeklyAnalytics.route) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Analyze Trends",
                                    fontSize = 12.sp,
                                    color = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // ──── ChatBot mini card ────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                color = TextDark
                            )
                            Spacer(Modifier.height(8.dp))

                            Icon(
                                painterResource(com.sugarcare.app.R.drawable.ic_chat),
                                null,
                                tint = OrangeDrop,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(GreenAccent, GreenAccent3)
                                        )
                                    )
                                    .clickable { navController.navigate(Screen.ChatScreen.route) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Get AI Advice !",
                                    fontSize = 12.sp,
                                    color = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                        onButtonClick = { navController.navigate(Screen.MealPlan.route) }
                    )

                    // Medication Plan mini card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Medication Plan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Spacer(Modifier.height(8.dp))
                            MedToggleRow("Insulin", insulinEnabled) { insulinEnabled = it }
                            MedToggleRow("Metformin", metforminEnabled) {
                                metforminEnabled = it
                            }
                            MedToggleRow("Notifications", notifEnabled) {
                                notifEnabled = it
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(GreenAccent, GreenAccent3)
                                        )
                                    )
                                    .clickable { navController.navigate(Screen.Medications.route) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Set Notifications",
                                    fontSize = 12.sp,
                                    color = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // ──── Sugar Counter mini card ────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                color = TextDark
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
                                    modifier = Modifier.clickable { navController.navigate(Screen.CounterScreen.route) },
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
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(GreenAccent, GreenAccent3)
                        )
                    )
                    .clickable { onButtonClick() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(buttonText, fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
            }

        }
    }
}


@Composable
private fun MedToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextDark)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
        )
    }
}
