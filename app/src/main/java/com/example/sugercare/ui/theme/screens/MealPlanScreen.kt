package com.sugarcare.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*

data class MealItem(val type: String, val name: String)

/**
 * Meal Plan Screen – shows personalized meal suggestions for
 * Breakfast, Lunch, and Dinner, with an "Add Reading" CTA.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(navController: NavHostController) {
    val meals = listOf(
        MealItem("Breakfast", "Oatmeal with Berries"),
        MealItem("Lunch",     "Grilled Chicken Salad"),
        MealItem("Dinner",    "Grilled Chicken Salad")
    )

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = "Your Personalized\nMeal Plan",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TealDark,
                    fontWeight = FontWeight.Bold,
                    textAlign  = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                // Add Reading button (green pill)
                Button(
                    onClick  = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp),
                    shape    = RoundedCornerShape(24.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                ) {
                    Text("Add Reading", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── General info card ─────────────────────────
                SugarCareCard {
                    Text(
                        text     = "General: [Doctor's overall diet type, e.g., Low Carb] Morning Planning | Haditime]",
                        fontSize = 13.sp,
                        color    = TextMedium
                    )
                }

                // ── Meal Suggestions section ──────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "Meal Suggestions",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark
                    )
                    Icon(Icons.Filled.Restaurant, null, tint = TealPrimary)
                }

                // Meal cards row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    meals.forEach { meal ->
                        MealCard(
                            modifier = Modifier.weight(1f),
                            type     = meal.type,
                            name     = meal.name
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            // ── Bottom nav ────────────────────────────────────
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick  = { navController.popBackStack() },
                    icon     = { Icon(Icons.Filled.Restaurant, null) },
                    label    = { Text("Suggestions") }
                )
            }
        }
    }
}

@Composable
private fun MealCard(modifier: Modifier, type: String, name: String) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier            = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(type, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
            Text(type, fontSize = 11.sp, color = TextLight)

            Spacer(Modifier.height(8.dp))

            // Placeholder food image circle
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(TealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Restaurant, null, tint = TealPrimary, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text      = name,
                fontSize  = 11.sp,
                color     = TextMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealPlanScreenPreview() {
    SugarCareTheme {
        MealPlanScreen(navController = rememberNavController())
    }
}
