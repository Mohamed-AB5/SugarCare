package com.sugarcare.app.ui.screens

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
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*

data class DayReading(val day: String, val value: Int)

/**
 * Weekly Analytics Screen – animated bar chart of average glucose
 * readings per day, with weekly average and most-frequent read time.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyAnalyticsScreen(navController: NavHostController) {
    val readings = listOf(
        DayReading("Mon", 122),
        DayReading("Tue", 145),
        DayReading("Wed", 108),
        DayReading("Thu", 127),
        DayReading("Fri", 136),
        DayReading("Sat", 131),
        DayReading("Sun", 115),
    )
    val maxVal = readings.maxOf { it.value }.toFloat()

    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header ────────────────────────────────────────
            Text(
                text       = "Weekly Analytics",
                style      = MaterialTheme.typography.headlineMedium,
                modifier   = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 8.dp),
                color      = TealPrimary,
                fontWeight = FontWeight.Bold
            )

            Icon(
                Icons.Filled.BarChart,
                null,
                tint     = TealPrimary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(40.dp)
            )

            Spacer(Modifier.height(8.dp))

            // ── Chart card ────────────────────────────────────
            SugarCareCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "Average Glucose",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(16.dp))

                // Y-axis labels + bars
                Row(
                    modifier             = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    verticalAlignment    = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Y axis
                    Column(
                        modifier              = Modifier
                            .fillMaxHeight()
                            .width(32.dp),
                        verticalArrangement   = Arrangement.SpaceBetween,
                        horizontalAlignment   = Alignment.End
                    ) {
                        listOf(160, 140, 120, 100, 80).forEach { label ->
                            Text(label.toString(), fontSize = 10.sp, color = TextLight)
                        }
                    }

                    // Bars
                    readings.forEach { reading ->
                        val targetFraction = reading.value / maxVal
                        val fraction by animateFloatAsState(
                            targetValue = if (animate) targetFraction else 0f,
                            animationSpec = tween(durationMillis = 800),
                            label = "bar_${reading.day}"
                        )
                        Column(
                            modifier            = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .fillMaxHeight(fraction)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(TealPrimary)
                            )
                        }
                    }
                }

                // X-axis day labels
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    readings.forEach { r ->
                        Text(r.day, fontSize = 11.sp, color = TextMedium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Summary stats ─────────────────────────────────
            SugarCareCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = TealLight.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text("Weekly Average: ", fontSize = 14.sp, color = TextDark)
                    Text("122 mg/dL", fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = TealLight.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Most Frequent Reading Time:", fontSize = 14.sp, color = TextDark)
                        Text(
                            "8:30 AM (Pre-Meal)",
                            fontSize   = 14.sp,
                            color      = TextDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Analyze Trends button ─────────────────────────
            Button(
                onClick  = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape    = RoundedCornerShape(28.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = TealDark)
            ) {
                Text("Analyze Trends", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.Add, null)
            }

            Spacer(Modifier.height(8.dp))

            // ── Bottom Nav ────────────────────────────────────
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home",    Icons.Filled.Home,    Screen.Home.route),
                    Triple("Meds",    Icons.Filled.Medication, Screen.Medications.route),
                    Triple("Trends",  Icons.Filled.BarChart, Screen.WeeklyAnalytics.route),
                    Triple("Profile", Icons.Filled.Person,  Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == Screen.WeeklyAnalytics.route,
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
