package com.sugarcare.app.ui.screens

// ─────────────────────────────────────────────────────────────
//  Feature 1 – Doctor Meal Plan
// ─────────────────────────────────────────────────────────────

/**
 * Represents a single meal entry prescribed by the doctor.
 *
 * Firestore-ready: every field maps 1-to-1 with a Firestore document field.
 * To connect later, replace [com.example.sugercare.data.meal.LocalMealData] with a Firestore fetch that
 * returns List<DoctorMeal> — this class stays unchanged.
 */
data class DoctorMeal(
    val mealType: String,       // "Breakfast" | "Lunch" | "Dinner" | "Snack"
    val name: String,           // e.g. "Oatmeal with Berries"
    val suggestedFoods: List<String>,  // e.g. ["Rolled oats", "Blueberries", "Almond milk"]
    val calories: Int?,         // nullable — doctor may not specify
    val time: String,           // e.g. "7:00 AM"
    val doctorNotes: String     // e.g. "Avoid adding sugar"
)

// ─────────────────────────────────────────────────────────────
//  Feature 2 – Meal Suggestions
// ─────────────────────────────────────────────────────────────

/**
 * A diabetes-friendly meal suggestion shown to the user.
 *
 * Firestore-ready: same swap strategy as [DoctorMeal].
 */
data class MealSuggestion(
    val name: String,           // e.g. "Grilled Salmon with Vegetables"
    val category: String,       // "Breakfast" | "Lunch" | "Dinner" | "Snack"
    val ingredients: List<String>,
    val calories: Int,
    val carbsGrams: Int,        // low-carb filter uses this
    val proteinGrams: Int,
    val sugarGrams: Int,        // low-sugar filter uses this
    val benefits: String        // e.g. "High in omega-3, stabilizes blood sugar"
)

// ─────────────────────────────────────────────────────────────
//  Feature 3 – Meal Logging
// ─────────────────────────────────────────────────────────────

/**
 * A record of what the user actually ate for a given meal slot today.
 *
 * Firestore-ready: when connecting to Firestore, save one document per
 * [MealLog] under  users/{uid}/mealLogs/{date}/{mealType}.
 * The data class requires no changes.
 */
data class MealLog(
    val mealType: String,       // "Breakfast" | "Lunch" | "Dinner" | "Snack"
    val selectedMealName: String,
    val completed: Boolean,
    val notes: String,          // optional user notes
    val loggedAt: String        // timestamp string, e.g. "2024-01-15 07:30"
)
