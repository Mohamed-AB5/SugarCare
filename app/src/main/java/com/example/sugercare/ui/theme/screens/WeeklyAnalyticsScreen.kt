package com.example.sugercare.ui.theme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.navigation.Screen
import com.sugarcare.app.ui.theme.*

data class DayReading(val day: String, val value: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyAnalyticsScreen(navController: NavHostController) {
    val readings = listOf(
        DayReading("Mon", 122), DayReading("Tue", 145), DayReading("Wed", 108),
        DayReading("Thu", 127), DayReading("Fri", 136), DayReading("Sat", 131),
        DayReading("Sun", 115)
    )
    val maxVal = readings.maxOf { it.value }.toFloat()
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    val isDark    = LocalDarkTheme.current.value
    val bgColor   = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark    else Color.White
    val textColor = if (isDark) TextDarkMode   else TextDark
    val lightText = if (isDark) TextMediumDark else TextMedium
    val statBg    = if (isDark) TealPrimary.copy(0.15f) else TealPrimary.copy(0.08f)
    val navColor  = if (isDark) SurfaceDark    else Color.White
    val navText   = if (isDark) TextMediumDark else TextMedium

    Column(Modifier.fillMaxSize().background(bgColor)) {

        // ── Header ────────────────────────────────────────────
        Text("Weekly Analytics",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 8.dp),
            color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 26.sp)

        Icon(Icons.Filled.BarChart, null, tint = TealPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally).size(40.dp))

        Spacer(Modifier.height(12.dp))

        // ── Chart Card ────────────────────────────────────────
        Card(
            Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Average Glucose",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = textColor, fontSize = 20.sp)
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth().height(160.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.fillMaxHeight().width(36.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        listOf(160, 140, 120, 100, 80).forEach { label ->
                            Text(label.toString(), fontSize = 11.sp, color = lightText)
                        }
                    }
                    readings.forEach { reading ->
                        val fraction by animateFloatAsState(
                            targetValue   = if (animate) reading.value / maxVal else 0f,
                            animationSpec = tween(800),
                            label         = "bar_${reading.day}"
                        )
                        Column(Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                Modifier.fillMaxWidth(0.65f).fillMaxHeight(fraction)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(TealPrimary)
                            )
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(start = 40.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    readings.forEach { r ->
                        Text(r.day, fontSize = 12.sp, color = lightText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Summary Card ──────────────────────────────────────
        Card(
            Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth()
                        .background(statBg, RoundedCornerShape(12.dp)).padding(14.dp)
                ) {
                    Text("Weekly Average: ", fontSize = 16.sp, color = textColor)
                    Text("122 mg/dL", fontSize = 16.sp,
                        color = TealPrimary, fontWeight = FontWeight.Bold)
                }
                Column(
                    Modifier.fillMaxWidth()
                        .background(statBg, RoundedCornerShape(12.dp)).padding(14.dp)
                ) {
                    Text("Most Frequent Reading Time:",
                        fontSize = 15.sp, color = textColor)
                    Text("8:30 AM (Pre-Meal)", fontSize = 16.sp,
                        color = TealPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // ── Analyze Trends button ─────────────────────────────
        Button(
            onClick  = {},
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
            shape    = RoundedCornerShape(28.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = TealDark),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text("Analyze Trends", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.Add, null)
        }

        Spacer(Modifier.height(8.dp))

        // ── Bottom Nav ────────────────────────────────────────
        NavigationBar(containerColor = navColor, tonalElevation = 0.dp) {
            listOf(
                Triple("Home",    Icons.Filled.Home,       Screen.Home.route),
                Triple("Logs",    Icons.Filled.Favorite,   Screen.Logs.route),
                Triple("Meals",   Icons.Filled.Restaurant, Screen.MealPlan.route),
                Triple("Profile", Icons.Filled.Person,     Screen.Profile.route)
            ).forEach { (label, icon, route) ->
                NavigationBarItem(
                    selected = route == Screen.WeeklyAnalytics.route,
                    onClick  = { navController.navigate(route) { launchSingleTop = true } },
                    icon     = { Icon(icon, null) },
                    label    = { Text(label, fontSize = 12.sp) },
                    colors   = NavigationBarItemDefaults.colors(
                        selectedIconColor    = TealPrimary,
                        unselectedIconColor  = navText,
                        indicatorColor       = TealPrimary.copy(0.2f)
                    )
                )
            }
        }
    }
}