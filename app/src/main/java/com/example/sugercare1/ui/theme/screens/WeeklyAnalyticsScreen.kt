
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*

data class DayReading(val day: String, val value: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyAnalyticsScreen(navController: NavHostController) {
    val readings = listOf(
        DayReading("Mon", 122), DayReading("Tue", 145), DayReading("Wed", 108),
        DayReading("Thu", 127), DayReading("Fri", 136), DayReading("Sat", 131), DayReading("Sun", 115)
    )
    val maxVal = readings.maxOf { it.value }.toFloat()
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Weekly Analytics",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 8.dp),
                color = TealPrimary, fontWeight = FontWeight.Bold)

            Icon(Icons.Filled.BarChart, null, tint = TealPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally).size(40.dp))

            Spacer(Modifier.height(8.dp))

            SugarCareCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Average Glucose", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth().height(160.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.fillMaxHeight().width(32.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        listOf(160, 140, 120, 100, 80).forEach { label ->
                            Text(label.toString(), fontSize = 10.sp, color = TextLight)
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
                            Box(Modifier.fillMaxWidth(0.6f).fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(TealPrimary))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(start = 36.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    readings.forEach { Text(it.day, fontSize = 11.sp, color = TextMedium) }
                }
            }

            Spacer(Modifier.height(16.dp))

            SugarCareCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(Modifier.fillMaxWidth()
                    .background(TealLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                ) {
                    Text("Weekly Average: ", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("122 mg/dL", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(8.dp))
                Column(Modifier.fillMaxWidth()
                    .background(TealLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                ) {
                    Text("Most Frequent Reading Time:", fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("8:30 AM (Pre-Meal)", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealDark),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Analyze Trends", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.Add, null)
            }

            Spacer(Modifier.height(8.dp))

            BottomNavBar(navController, Screen.WeeklyAnalytics.route)
        }
    }
}
