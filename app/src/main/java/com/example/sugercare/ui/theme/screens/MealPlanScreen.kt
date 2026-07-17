package com.example.sugercare.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.viewModels.MealViewModel
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareBottomBar
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*
import com.example.sugercare.ui.screens.MyMealsTab
import com.example.sugercare.viewModels.MealCrudViewModel
import com.sugarcare.app.ui.screens.DoctorMeal
import com.sugarcare.app.ui.screens.MealSuggestion
import com.sugarcare.app.ui.screens.MealLog

// ─────────────────────────────────────────────────────────────
//  MealPlanScreen — 2 tabs: Doctor Plan | Suggestions
// ─────────────────────────────────────────────────────────────

@Composable
fun MealPlanScreen(
    navController: NavHostController,
    mealViewModel: MealViewModel = viewModel()
) {
    val tabs = listOf(
        "Doctor Plan",
        "Suggestions",
        "My Meals"
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = "Your Personalized\nMeal Plan",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TealDark,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            }

            // ── Tab Row ───────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = BackgroundLight,
                contentColor     = TealPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        text     = {
                            Text(
                                text       = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold
                                else FontWeight.Normal,
                                fontSize   = 14.sp
                            )
                        }
                    )
                }
            }

            // ── Tab Content ───────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> DoctorPlanTab(mealViewModel)
                    1 -> SuggestionsTab(mealViewModel)
                    2 -> MyMealsTab(
                        viewModel = viewModel<MealCrudViewModel>()
                    )
                }
            }

            // ── Bottom Nav ────────────────────────────────────
            // MealPlan is not one of the four fixed nav items,
            // so currentRoute = MealPlan.route → nothing is selected.
            SugarCareBottomBar(
                currentRoute = Screen.MealPlan.route,
                onHome       = { navController.navigate(Screen.Home.route) },
                onMeds       = { navController.navigate(Screen.Medications.route) },
                onTrends     = { navController.navigate(Screen.WeeklyAnalytics.route) },
                onProfile    = { navController.navigate(Screen.Profile.route) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Tab 1 — Doctor Plan
// ─────────────────────────────────────────────────────────────

@Composable
private fun DoctorPlanTab(mealViewModel: MealViewModel) {
    val doctorMeals by mealViewModel.doctorMeals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // General info card
        SugarCareCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint     = TealPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text     = "Diet type: Low Carb  •  Prepared by your doctor",
                    fontSize = 13.sp,
                    color    = TextMedium
                )
            }
        }

        Text(
            text       = "Today's Meal Plan",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = TextDark
        )

        doctorMeals.forEach { meal ->
            DoctorMealCard(meal = meal)
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DoctorMealCard(meal: DoctorMeal) {
    SugarCareCard {
        // Meal type chip + time row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = TealPrimary
            ) {
                Text(
                    text       = meal.mealType,
                    color      = Color.White,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = null,
                    tint     = TextMedium,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(text = meal.time, fontSize = 13.sp, color = TextMedium)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Meal name
        Text(
            text       = meal.name,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = TextDark
        )

        Spacer(Modifier.height(6.dp))

        // Calories — only shown when available
        meal.calories?.let { cal ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint     = GreenAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text       = "$cal kcal",
                    fontSize   = 13.sp,
                    color      = GreenAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(6.dp))
        }

        // Suggested foods
        Text(
            text       = "Suggested foods:",
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextDark
        )
        meal.suggestedFoods.forEach { food ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    Icons.Filled.FiberManualRecord,
                    contentDescription = null,
                    tint     = TealPrimary,
                    modifier = Modifier.size(8.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(text = food, fontSize = 13.sp, color = TextMedium)
            }
        }

        // Doctor notes
        if (meal.doctorNotes.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TealLight.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier          = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.StickyNote2,
                        contentDescription = null,
                        tint     = TealDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text     = meal.doctorNotes,
                        fontSize = 12.sp,
                        color    = TealDark
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Tab 2 — Meal Suggestions
// ─────────────────────────────────────────────────────────────

@Composable
private fun SuggestionsTab(mealViewModel: MealViewModel) {
    val suggestions      by mealViewModel.filteredSuggestions.collectAsState()
    val selectedCategory by mealViewModel.selectedCategory.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Category filter chips ─────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            mealViewModel.categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick  = { mealViewModel.filterByCategory(category) },
                    label    = { Text(category, fontSize = 13.sp) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor     = Color.White,
                        containerColor         = BackgroundLight,
                        labelColor             = TextMedium
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled             = true,
                        selected            = selectedCategory == category,
                        borderColor         = TealLight,
                        selectedBorderColor = TealPrimary
                    )
                )
            }
        }

        // ── Suggestion cards ──────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            suggestions.forEach { suggestion ->
                SuggestionCard(suggestion = suggestion)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: MealSuggestion) {
    SugarCareCard {
        // Name + category chip
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = suggestion.name,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = TextDark,
                modifier   = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = GreenAccent.copy(alpha = 0.15f)
            ) {
                Text(
                    text       = suggestion.category,
                    fontSize   = 11.sp,
                    color      = GreenAccent,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Nutrition row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionChip(
                icon  = Icons.Filled.LocalFireDepartment,
                value = "${suggestion.calories}",
                unit  = "kcal",
                tint  = GreenAccent
            )
            NutritionChip(
                icon  = Icons.Filled.Grain,
                value = "${suggestion.carbsGrams}g",
                unit  = "carbs",
                tint  = TealPrimary
            )
            NutritionChip(
                icon  = Icons.Filled.FitnessCenter,
                value = "${suggestion.proteinGrams}g",
                unit  = "protein",
                tint  = TealDark
            )
            NutritionChip(
                icon  = Icons.Filled.WaterDrop,
                value = "${suggestion.sugarGrams}g",
                unit  = "sugar",
                tint  = TextMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        // Ingredients
        Text(
            text       = "Ingredients:",
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextDark
        )
        Text(
            text     = suggestion.ingredients.joinToString(" • "),
            fontSize = 12.sp,
            color    = TextMedium,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(Modifier.height(6.dp))

        // Benefits
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = TealLight.copy(alpha = 0.3f)
        ) {
            Row(
                modifier          = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint     = TealDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text     = suggestion.benefits,
                    fontSize = 12.sp,
                    color    = TealDark
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Shared small composable
// ─────────────────────────────────────────────────────────────

@Composable
private fun NutritionChip(
    icon:  ImageVector,
    value: String,
    unit:  String,
    tint:  Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tint)
        Text(text = unit,  fontSize = 10.sp, color = TextLight)
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun MealPlanScreenPreview() {
    SugarCareTheme {
        MealPlanScreen(navController = rememberNavController())
    }
}
