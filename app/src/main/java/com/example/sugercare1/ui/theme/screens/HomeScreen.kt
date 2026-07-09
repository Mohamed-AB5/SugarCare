package com.sugarcare.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareTextField
import com.sugarcare.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var insulinEnabled   by remember { mutableStateOf(true) }
    var metforminEnabled by remember { mutableStateOf(true) }
    var notifEnabled     by remember { mutableStateOf(false) }
    var isDarkTheme      by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    
    val sharedPref = remember { context.getSharedPreferences("EmergencyPrefs", Context.MODE_PRIVATE) }
    
    
    var savedName by remember { mutableStateOf(sharedPref.getString("em_name", "") ?: "") }
    var savedRelation by remember { mutableStateOf(sharedPref.getString("em_relation", "") ?: "") }
    var savedPhone by remember { mutableStateOf(sharedPref.getString("em_phone", "123") ?: "123") }

    
    var showEmergencyMenu by remember { mutableStateOf(false) }
    var showEditContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Sugar Care", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(TealLight),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.Person, null, tint = TealPrimary) }
                    }
                },
                actions = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        modifier = Modifier.scale(0.8f)
                    )
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        BadgedBox(badge = { Badge(containerColor = OrangeDrop) { Text("3", fontSize = 9.sp) } }) {
                            Icon(Icons.Filled.Notifications, null, tint = OrangeDrop)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { BottomNavBar(navController, Screen.Home.route) },
        floatingActionButton = {
           
            FloatingActionButton(
                onClick = { showEmergencyMenu = true },
                containerColor = Color.Red,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "Emergency")
            }
        }
    ) { paddingValues ->
        SugarCareBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
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
                            
                            SmallGradientButton(
                                text = "Analyze Trends",
                                gradientColors = listOf(Color(0xFF65B96E), Color(0xFF9DF0A5)),
                                onClick = { navController.navigate(Screen.WeeklyAnalytics.route) }
                            )
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
                            
                            SmallGradientButton(
                                text = "Set Notifications",
                                gradientColors = listOf(Color(0xFF3B9E9E), Color(0xFF7FE3E1)),
                                onClick = { navController.navigate(Screen.Medications.route) }
                            )
                        }
                    }
                }
            }
        }
    }

   
    if (showEmergencyMenu) {
        AlertDialog(
            onDismissRequest = { showEmergencyMenu = false },
            title = { Text("Emergency Action", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                   
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:123"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Icon(Icons.Filled.LocalHospital, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Call Ambulance (123)")
                    }

               
                    if (savedName.isNotEmpty()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$savedPhone"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Call $savedName ($savedRelation)")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showEmergencyMenu = false
                    showEditContactDialog = true 
                }) {
                    Text(if (savedName.isEmpty()) "Add Contact" else "Edit Contact", color = TealPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyMenu = false }) { Text("Cancel", color = TextMedium) }
            }
        )
    }

    
    if (showEditContactDialog) {
    
        var inputName by remember { mutableStateOf(savedName) }
        var inputRelation by remember { mutableStateOf(savedRelation) }
        var inputPhone by remember { mutableStateOf(if (savedPhone == "123") "" else savedPhone) }

        AlertDialog(
            onDismissRequest = { showEditContactDialog = false },
            title = { Text("Personal Emergency Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SugarCareTextField(value = inputName, onValueChange = { inputName = it }, label = "Contact Name (e.g. Ali)")
                    SugarCareTextField(value = inputRelation, onValueChange = { inputRelation = it }, label = "Relation (e.g. Brother)")
                    SugarCareTextField(value = inputPhone, onValueChange = { inputPhone = it }, label = "Phone Number")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        
                        savedName = inputName
                        savedRelation = inputRelation
                        savedPhone = inputPhone.ifEmpty { "123" }

                        with(sharedPref.edit()) {
                            putString("em_name", savedName)
                            putString("em_relation", savedRelation)
                            putString("em_phone", savedPhone)
                            apply() 
                        }
                        showEditContactDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditContactDialog = false }) { Text("Cancel", color = TextMedium) }
            }
        )
    }
}

@Composable
fun SmallGradientButton(text: String, gradientColors: List<Color>, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.background(Brush.horizontalGradient(colors = gradientColors), RoundedCornerShape(20.dp)).fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) { Text(text = text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
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
private fun DashboardCard(modifier: Modifier, title: String, subtitle: String, icon: ImageVector, iconTint: Color, buttonText: String, onButtonClick: () -> Unit) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle.isNotEmpty()) { Spacer(Modifier.height(4.dp)); Text(subtitle, fontSize = 11.sp, color = TextMedium) }
            Spacer(Modifier.height(8.dp)); Icon(icon, null, tint = iconTint, modifier = Modifier.size(44.dp)); Spacer(Modifier.height(8.dp))
            SmallGradientButton(text = buttonText, gradientColors = listOf(Color(0xFF65B96E), Color(0xFF9DF0A5)), onClick = onButtonClick)
        }
    }
}

@Composable
private fun MedToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary, checkedTrackColor = TealLight))
    }
}
