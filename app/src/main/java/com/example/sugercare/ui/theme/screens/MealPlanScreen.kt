package com.example.sugercare.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sugarcare.app.ui.theme.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import com.sugarcare.app.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(navController: NavHostController) {
    val meals = listOf(
        Triple("Breakfast", "Breakfast", "Oatmeal with Berries"),
        Triple("Lunch",     "Lunch",     "Grilled Chicken Salad"),
        Triple("Dinner",    "Dinner",    "Grilled Chicken Salad")
    )

    val isDark     = LocalDarkTheme.current.value
    val bgColor    = if (isDark) BackgroundDark  else BackgroundLight
    val cardColor  = if (isDark) SurfaceDark     else Color.White
    val titleColor = if (isDark) TextDarkMode    else TealDark
    val textColor  = if (isDark) TextDarkMode    else TextDark
    val subColor   = if (isDark) TextMediumDark  else TextMedium
    val navColor   = if (isDark) SurfaceDark     else Color.White
    val navText    = if (isDark) TextMediumDark  else TextMedium
    // Header stays a branded teal banner in both modes (decorative, not a content card)
    val headerBg   = if (isDark) SurfaceDark     else TealDark

    Column(Modifier.fillMaxSize().background(bgColor)) {

        // ── Header ────────────────────────────────────────────
        Column(
            Modifier.fillMaxWidth().background(headerBg).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your Personalized\nMeal Plan",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, fontSize = 26.sp)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.8f).height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) { Text("Add Reading", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    "General: [Doctor's overall diet type, e.g., Low Carb]\nMorning Planning | Haditime]",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp, color = subColor, lineHeight = 22.sp
                )
            }

            // Meal Suggestions header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Meal Suggestions",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                Icon(Icons.Filled.Restaurant, null, tint = TealPrimary)
            }

            // Meal cards row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                meals.forEach { (type, label, name) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(type, fontWeight = FontWeight.Bold,
                                fontSize = 15.sp, color = textColor)
                            Text(label, fontSize = 12.sp, color = subColor)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                Modifier.size(56.dp).clip(CircleShape).background(TealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Restaurant, null,
                                    tint = TealPrimary, modifier = Modifier.size(28.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(name, fontSize = 12.sp, color = subColor,
                                textAlign = TextAlign.Center, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }

        // ── Bottom Nav ────────────────────────────────────────
        NavigationBar(containerColor = navColor, tonalElevation = 0.dp) {
            listOf(
                Triple("Home",    Icons.Filled.Home,       Screen.Home.route),
                Triple("Logs",    Icons.Filled.Favorite,   Screen.Logs.route),
                Triple("Meals",   Icons.Filled.Restaurant, Screen.MealPlan.route),
                Triple("Profile", Icons.Filled.Person,     Screen.Profile.route)
            ).forEach { (label, icon, route) ->
                NavigationBarItem(
                    selected = route == Screen.MealPlan.route,
                    onClick  = { navController.navigate(route) { launchSingleTop = true } },
                    icon     = { Icon(icon, null) },
                    label    = { Text(label, fontSize = 12.sp) },
                    colors   = NavigationBarItemDefaults.colors(
                        selectedIconColor   = TealPrimary,
                        unselectedIconColor = navText,
                        indicatorColor      = TealPrimary.copy(0.2f)
                    )
                )
            }
        }
    }
}