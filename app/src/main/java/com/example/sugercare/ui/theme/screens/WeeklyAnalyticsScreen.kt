package com.sugarcare.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*
import com.example.sugercare.app.SugarViewModel
import com.example.sugercare.app.parseReadingNote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DayReading(val day: String, val value: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyAnalyticsScreen(
    navController: NavHostController,
    viewModel: SugarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val dbReadings = viewModel.readingsList.value
    var isWeekly by remember { mutableStateOf(true) }

    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Group readings and calculate averages dynamically based on mode
    val readings = remember(dbReadings, isWeekly) {
        val now = System.currentTimeMillis()
        if (isWeekly) {
            val weeklyReadings = dbReadings.filter { it.timestamp >= now - (7L * 24 * 60 * 60 * 1000) }
            val groupedAverages = weeklyReadings.groupBy {
                SimpleDateFormat("E", Locale.getDefault()).format(Date(it.timestamp))
            }.mapValues { entry ->
                entry.value.map { it.glucoseLevel }.average().toInt()
            }
            dayNames.map { day ->
                DayReading(day, groupedAverages[day] ?: 0)
            }
        } else {
            val monthlyReadings = dbReadings.filter { it.timestamp >= now - (30L * 24 * 60 * 60 * 1000) }
            val w1Readings = monthlyReadings.filter { it.timestamp >= now - (30L * 24 * 60 * 60 * 1000) && it.timestamp < now - (22L * 24 * 60 * 60 * 1000) }
            val w2Readings = monthlyReadings.filter { it.timestamp >= now - (22L * 24 * 60 * 60 * 1000) && it.timestamp < now - (15L * 24 * 60 * 60 * 1000) }
            val w3Readings = monthlyReadings.filter { it.timestamp >= now - (15L * 24 * 60 * 60 * 1000) && it.timestamp < now - (7L * 24 * 60 * 60 * 1000) }
            val w4Readings = monthlyReadings.filter { it.timestamp >= now - (7L * 24 * 60 * 60 * 1000) }

            val w1Avg = if (w1Readings.isNotEmpty()) w1Readings.map { it.glucoseLevel }.average().toInt() else 0
            val w2Avg = if (w2Readings.isNotEmpty()) w2Readings.map { it.glucoseLevel }.average().toInt() else 0
            val w3Avg = if (w3Readings.isNotEmpty()) w3Readings.map { it.glucoseLevel }.average().toInt() else 0
            val w4Avg = if (w4Readings.isNotEmpty()) w4Readings.map { it.glucoseLevel }.average().toInt() else 0

            listOf(
                DayReading("Week 1", w1Avg),
                DayReading("Week 2", w2Avg),
                DayReading("Week 3", w3Avg),
                DayReading("Week 4", w4Avg)
            )
        }
    }

    val filteredRangeReadings = remember(dbReadings, isWeekly) {
        val now = System.currentTimeMillis()
        if (isWeekly) {
            dbReadings.filter { it.timestamp >= now - (7L * 24 * 60 * 60 * 1000) }
        } else {
            dbReadings.filter { it.timestamp >= now - (30L * 24 * 60 * 60 * 1000) }
        }
    }

    val maxVal = 250f
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(isWeekly) {
        animate = false
        animate = true
    }

    val isDark = LocalDarkTheme.current.value
    val bgColor = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor = if (isDark) Color(0xFF80CBC4) else TextMedium
    val navColor = if (isDark) SurfaceDark else Color.White
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                if (isWeekly) "Weekly Analytics" else "Monthly Analytics",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 8.dp),
                color = TealPrimary, fontWeight = FontWeight.Bold
            )

            Icon(
                Icons.Filled.BarChart, null, tint = TealPrimary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(40.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Smooth sliding weekly/monthly view switcher toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TealLight.copy(alpha = 0.3f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isWeekly) TealPrimary else Color.Transparent)
                        .clickable { isWeekly = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Weekly",
                        color = if (isWeekly) Color.White else TealDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isWeekly) TealPrimary else Color.Transparent)
                        .clickable { isWeekly = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Monthly",
                        color = if (!isWeekly) Color.White else TealDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            SugarCareCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "Average Glucose", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        Modifier
                            .fillMaxHeight()
                            .width(32.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        listOf(250, 200, 150, 100, 50).forEach { label ->
                            Text(label.toString(), fontSize = 10.sp, color = TextLight)
                        }
                    }
                    readings.forEach { reading ->
                        val fraction by animateFloatAsState(
                            targetValue = if (animate) reading.value / maxVal else 0f,
                            animationSpec = tween(800),
                            label = "bar_${reading.day}"
                        )
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(0.6f)
                                    .fillMaxHeight(fraction)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(TealPrimary)
                            )
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    readings.forEach { Text(it.day, fontSize = 11.sp, color = TextMedium) }
                }
            }

            Spacer(Modifier.height(16.dp))

            SugarCareCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(TealLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        if (isWeekly) "Weekly Average: " else "Monthly Average: ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val avgDisplay = if (filteredRangeReadings.isNotEmpty()) {
                        "${filteredRangeReadings.map { it.glucoseLevel }.average().toInt()} mg/dL"
                    } else {
                        "No readings"
                    }
                    Text(
                        avgDisplay, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(8.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(TealLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "Most Frequent Meal Type:", fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val mealCounts = filteredRangeReadings.mapNotNull {
                        val (meal, _, _) = parseReadingNote(it.note)
                        meal
                    }.groupingBy { it }.eachCount()
                    val mostFrequentMeal = mealCounts.maxByOrNull { it.value }?.key
                    val freqDisplay = if (mostFrequentMeal != null) {
                        "$mostFrequentMeal Meal"
                    } else {
                        "No meal data"
                    }
                    Text(
                        freqDisplay, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(TealDark, TealPrimary2)
                        )
                    )
                    .clickable { navController.navigate(Screen.Logs.route) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Track Glucose",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = White
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            NavigationBar(containerColor = navColor, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home", Icons.Filled.Home, Screen.Home.route),
                    Triple("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                    Triple("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
                    Triple("Profile", Icons.Filled.Person, Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == Screen.Logs.route,
                        onClick = {
                            if (route != Screen.Logs.route) navController.navigate(route) {
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
        }
    }
}
