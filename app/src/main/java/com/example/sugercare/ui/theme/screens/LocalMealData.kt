package com.sugarcare.app.ui.screens

/**
 * Local data source for meal plan and suggestions.
 *
 * ── HOW TO CONNECT TO FIRESTORE LATER ──────────────────────────
 * 1. Create FirestoreMealData.kt with the same two functions:
 *      suspend fun getDoctorMealPlan(): List<DoctorMeal>
 *      fun getMealSuggestions(): List<MealSuggestion>
 * 2. In MealViewModel, replace:
 *      LocalMealData.getDoctorMealPlan()
 *    with:
 *      FirestoreMealData.getDoctorMealPlan()
 * 3. Delete or keep this file as a fallback.
 * The UI (MealPlanScreen) and ViewModel require zero changes.
 * ───────────────────────────────────────────────────────────────
 */
object LocalMealData {

    // ── Feature 1: Doctor Meal Plan ───────────────────────────
    fun getDoctorMealPlan(): List<DoctorMeal> = listOf(
        DoctorMeal(
            mealType       = "Breakfast",
            name           = "Oatmeal with Berries",
            suggestedFoods = listOf("Rolled oats", "Blueberries", "Almond milk", "Chia seeds"),
            calories       = 320,
            time           = "7:00 AM",
            doctorNotes    = "Use unsweetened almond milk. Avoid adding sugar or honey."
        ),
        DoctorMeal(
            mealType       = "Lunch",
            name           = "Grilled Chicken Salad",
            suggestedFoods = listOf("Grilled chicken breast", "Romaine lettuce",
                "Cherry tomatoes", "Cucumber", "Olive oil dressing"),
            calories       = 420,
            time           = "1:00 PM",
            doctorNotes    = "Use olive oil and lemon dressing only. No croutons."
        ),
        DoctorMeal(
            mealType       = "Snack",
            name           = "Handful of Almonds",
            suggestedFoods = listOf("Raw almonds", "Celery sticks"),
            calories       = 160,
            time           = "4:00 PM",
            doctorNotes    = "Keep portion to 20 almonds. Good for blood sugar stability."
        ),
        DoctorMeal(
            mealType       = "Dinner",
            name           = "Baked Salmon with Vegetables",
            suggestedFoods = listOf("Salmon fillet", "Steamed broccoli",
                "Sweet potato (small)", "Lemon"),
            calories       = 510,
            time           = "7:30 PM",
            doctorNotes    = "Bake at 200°C for 20 min. Avoid frying. Limit sweet potato to half."
        )
    )

    // ── Feature 2: Diabetes-Friendly Meal Suggestions ─────────
    fun getMealSuggestions(): List<MealSuggestion> = listOf(

        // ── Breakfast ─────────────────────────────────────────
        MealSuggestion(
            name          = "Greek Yogurt Parfait",
            category      = "Breakfast",
            ingredients   = listOf("Plain Greek yogurt", "Walnuts", "Cinnamon", "Strawberries"),
            calories      = 280,
            carbsGrams    = 18,
            proteinGrams  = 20,
            sugarGrams    = 9,
            benefits      = "High protein, low glycemic index, supports satiety"
        ),
        MealSuggestion(
            name          = "Veggie Egg Scramble",
            category      = "Breakfast",
            ingredients   = listOf("Eggs", "Spinach", "Bell peppers", "Olive oil", "Feta cheese"),
            calories      = 310,
            carbsGrams    = 8,
            proteinGrams  = 22,
            sugarGrams    = 4,
            benefits      = "Zero sugar, high protein, rich in vitamins"
        ),
        MealSuggestion(
            name          = "Avocado Toast on Whole Grain",
            category      = "Breakfast",
            ingredients   = listOf("Whole grain bread", "Avocado", "Lemon juice", "Pumpkin seeds"),
            calories      = 340,
            carbsGrams    = 28,
            proteinGrams  = 10,
            sugarGrams    = 3,
            benefits      = "Healthy fats, slow-release carbs, stabilizes blood sugar"
        ),

        // ── Lunch ─────────────────────────────────────────────
        MealSuggestion(
            name          = "Lentil Soup",
            category      = "Lunch",
            ingredients   = listOf("Red lentils", "Carrots", "Cumin", "Tomatoes", "Onion"),
            calories      = 370,
            carbsGrams    = 42,
            proteinGrams  = 18,
            sugarGrams    = 6,
            benefits      = "High fiber, lowers post-meal blood sugar spikes"
        ),
        MealSuggestion(
            name          = "Turkey Lettuce Wraps",
            category      = "Lunch",
            ingredients   = listOf("Ground turkey", "Lettuce leaves", "Garlic",
                "Ginger", "Low-sodium soy sauce"),
            calories      = 290,
            carbsGrams    = 10,
            proteinGrams  = 30,
            sugarGrams    = 3,
            benefits      = "Very low carb, high lean protein"
        ),
        MealSuggestion(
            name          = "Quinoa & Chickpea Bowl",
            category      = "Lunch",
            ingredients   = listOf("Quinoa", "Chickpeas", "Cucumber", "Parsley", "Lemon dressing"),
            calories      = 400,
            carbsGrams    = 48,
            proteinGrams  = 16,
            sugarGrams    = 5,
            benefits      = "Complete protein, high fiber, low glycemic"
        ),

        // ── Dinner ────────────────────────────────────────────
        MealSuggestion(
            name          = "Baked Cod with Asparagus",
            category      = "Dinner",
            ingredients   = listOf("Cod fillet", "Asparagus", "Garlic", "Olive oil", "Lemon"),
            calories      = 350,
            carbsGrams    = 8,
            proteinGrams  = 38,
            sugarGrams    = 3,
            benefits      = "Very low carb, lean protein, heart-friendly"
        ),
        MealSuggestion(
            name          = "Chicken & Vegetable Stir-Fry",
            category      = "Dinner",
            ingredients   = listOf("Chicken breast", "Broccoli", "Snap peas",
                "Bell pepper", "Low-sodium soy sauce"),
            calories      = 390,
            carbsGrams    = 18,
            proteinGrams  = 35,
            sugarGrams    = 7,
            benefits      = "Balanced macros, low sugar, high protein"
        ),
        MealSuggestion(
            name          = "Cauliflower Rice Bowl",
            category      = "Dinner",
            ingredients   = listOf("Cauliflower rice", "Black beans", "Avocado",
                "Salsa", "Lime juice"),
            calories      = 320,
            carbsGrams    = 30,
            proteinGrams  = 14,
            sugarGrams    = 5,
            benefits      = "Low-carb rice alternative, fiber-rich"
        ),

        // ── Snack ─────────────────────────────────────────────
        MealSuggestion(
            name          = "Apple with Peanut Butter",
            category      = "Snack",
            ingredients   = listOf("Green apple", "Natural peanut butter (1 tbsp)"),
            calories      = 200,
            carbsGrams    = 22,
            proteinGrams  = 5,
            sugarGrams    = 14,
            benefits      = "Fiber + healthy fat slows sugar absorption"
        ),
        MealSuggestion(
            name          = "Hummus & Veggie Sticks",
            category      = "Snack",
            ingredients   = listOf("Hummus", "Carrot sticks", "Celery", "Cucumber"),
            calories      = 150,
            carbsGrams    = 14,
            proteinGrams  = 6,
            sugarGrams    = 4,
            benefits      = "Low calorie, high fiber, blood sugar friendly"
        )
    )
}
