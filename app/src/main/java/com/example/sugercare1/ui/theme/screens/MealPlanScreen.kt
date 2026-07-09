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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*

data class MealItem(val type: String, val name: String)

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
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Your Personalized\nMeal Plan",
                    style = MaterialTheme.typography.headlineMedium,
                   
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)

                Spacer(Modifier.height(16.dp))

                
                SugarCareGradientButton(
                    text = "Add Reading",
                    gradientColors = listOf(Color(0xFF65B96E), Color(0xFF9DF0A5)),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = { 
                    navController.navigate(Screen.Logs.route)
                    }
                )
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SugarCareCard {
                    Text("General: [Doctor's overall diet type, e.g., Low Carb] Morning Planning | Haditime]",
                        fontSize = 13.sp, color = TextMedium)
                }

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Meal Suggestions", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Icon(Icons.Filled.Restaurant, null, tint = TealPrimary)
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    meals.forEach { meal ->
                        MealCard(Modifier.weight(1f), meal.type, meal.name)
                    }
                }
            }

            //  Bottom Nav (Home/Logs/Meals/Profile)
            BottomNavBar(navController, Screen.MealPlan.route)
        }
    }
}

@Composable
private fun MealCard(modifier: Modifier, type: String, name: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(type, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface)
            Text(type, fontSize = 11.sp, color = TextLight)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.size(56.dp).clip(CircleShape).background(TealLight),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Restaurant, null, tint = TealPrimary, modifier = Modifier.size(28.dp)) }
            Spacer(Modifier.height(8.dp))
            Text(name, fontSize = 11.sp, color = TextMedium, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealPlanScreenPreview() {
    SugarCareTheme { MealPlanScreen(navController = rememberNavController()) }
}
