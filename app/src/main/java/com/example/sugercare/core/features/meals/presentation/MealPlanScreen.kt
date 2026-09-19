package com.example.sugercare.core.features.meals.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.core.features.meals.presentation.viewModel.MealCrudViewModel
import com.example.sugercare.core.features.meals.presentation.viewModel.MealViewModel
import com.example.sugercare.ui.screens.MyMealsTab
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.screens.DoctorMeal
import com.sugarcare.app.ui.screens.MealSuggestion
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SugarCareTheme
import com.sugarcare.app.ui.theme.SurfaceDark
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextLight
import com.sugarcare.app.ui.theme.TextMedium



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

    val isDark = LocalDarkTheme.current.value
    val bgColor = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor = if (isDark) Color(0xFF80CBC4) else TextMedium
    val navColor = if (isDark) SurfaceDark else Color.White
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = "Your Personalized\nMeal Plan",
                    style      = MaterialTheme.typography.headlineMedium,
                    
                    color      = if (isDark) Color(0xFF80CBC4) else TealDark,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            }

          
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = bgColor,
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

           
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> DoctorPlanTab(mealViewModel)
                    1 -> SuggestionsTab(mealViewModel)
                    2 -> MyMealsTab(
                        viewModel = viewModel<MealCrudViewModel>()
                    )
                }
            }

            
            NavigationBar(containerColor = navColor, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home", Icons.Filled.Home, Screen.Home.route),
                    Triple("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                    Triple("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
                    Triple("Profile", Icons.Filled.Person, Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == currentRoute,
                        onClick = {
                            if (route != Screen.MealPlan.route) navController.navigate(route) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp, color = textColor) },
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



@Composable
private fun DoctorPlanTab(mealViewModel: MealViewModel) {
    val doctorMeals by mealViewModel.doctorMeals.collectAsState()

    val isDark = LocalDarkTheme.current.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
  
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
                  
                    color    = if (isDark) Color(0xFF80CBC4) else TextMedium
                )
            }
        }

       
        Text(
            text       = "Today's Meal Plan",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = if (isDark) Color(0xFFE0F2F1) else TextDark
        )

        doctorMeals.forEach { meal ->
            DoctorMealCard(meal = meal)
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DoctorMealCard(meal: DoctorMeal) {
   
    val isDark    = LocalDarkTheme.current.value
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor  = if (isDark) Color(0xFF80CBC4) else TextMedium

    SugarCareCard {
  
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
                    tint     = subColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(text = meal.time, fontSize = 13.sp, color = subColor)
            }
        }

        Spacer(Modifier.height(8.dp))

      
        Text(
            text       = meal.name,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = textColor
        )

        Spacer(Modifier.height(6.dp))

        
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

        Text(
            text       = "Suggested foods:",
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = textColor
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
                Text(text = food, fontSize = 13.sp, color = subColor)
            }
        }

        if (meal.doctorNotes.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) TealPrimary.copy(alpha = 0.18f) else TealLight.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier          = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.StickyNote2,
                        contentDescription = null,
                        tint     = if (isDark) Color(0xFF80CBC4) else TealDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text     = meal.doctorNotes,
                        fontSize = 12.sp,
                        color    = if (isDark) Color(0xFF80CBC4) else TealDark
                    )
                }
            }
        }
    }
}


@Composable
private fun SuggestionsTab(mealViewModel: MealViewModel) {
    val suggestions      by mealViewModel.filteredSuggestions.collectAsState()
    val selectedCategory by mealViewModel.selectedCategory.collectAsState()
    val isDark = LocalDarkTheme.current.value

    Column(modifier = Modifier.fillMaxSize()) {

        
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
                        containerColor         = if (isDark) SurfaceDark else BackgroundLight,
                        labelColor             = if (isDark) Color(0xFF80CBC4) else TextMedium
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
    
    val isDark    = LocalDarkTheme.current.value
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor  = if (isDark) Color(0xFF80CBC4) else TextMedium

    SugarCareCard {
    
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = suggestion.name,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = textColor,
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
                tint  = subColor
            )
        }

        Spacer(Modifier.height(8.dp))

     
        Text(
            text       = "Ingredients:",
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = textColor
        )
        Text(
            text     = suggestion.ingredients.joinToString(" • "),
            fontSize = 12.sp,
            color    = subColor,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isDark) TealPrimary.copy(alpha = 0.18f) else TealLight.copy(alpha = 0.3f)
        ) {
            Row(
                modifier          = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint     = if (isDark) Color(0xFF80CBC4) else TealDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text     = suggestion.benefits,
                    fontSize = 12.sp,
                    color    = if (isDark) Color(0xFF80CBC4) else TealDark
                )
            }
        }
    }
}



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
        
        val isDark = LocalDarkTheme.current.value
        Text(text = unit,  fontSize = 10.sp, color = if (isDark) Color(0xFF80CBC4) else TextLight)
    }
}


@Preview(showBackground = true)
@Composable
fun MealPlanScreenPreview() {
    SugarCareTheme {
        MealPlanScreen(navController = rememberNavController())
    }
}
