package com.example.sugercare.data.meal

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for meal data.
 *
 * The ViewModel talks only to this class — never to MealDao directly.
 * This keeps the ViewModel testable and makes the Firestore upgrade
 * a one-file change: replace the MealDao calls below with Firestore
 * calls and the ViewModel + UI require zero changes.
 *
 * ── Firestore upgrade path ──────────────────────────────────────
 * Replace each function body with a Firestore call.
 * Return types stay identical (Flow / suspend).
 * ───────────────────────────────────────────────────────────────
 */
class MealRepository(context: Context) {

    private val dao: MealDao = MealDatabase.getInstance(context).mealDao()

    // ── Read ──────────────────────────────────────────────────

    /** Observe all meals — emits on every database change. */
    fun getAllMeals(): Flow<List<MealEntity>> = dao.getAllMeals()

    /** Observe meals filtered by [type]. */
    fun getMealsByType(type: String): Flow<List<MealEntity>> = dao.getMealsByType(type)

    /** Observe meals matching [query] in the meal name. */
    fun searchMeals(query: String): Flow<List<MealEntity>> = dao.searchMeals(query)

    // ── Write ─────────────────────────────────────────────────

    suspend fun insertMeal(meal: MealEntity) = dao.insertMeal(meal)

    suspend fun updateMeal(meal: MealEntity) = dao.updateMeal(meal)

    suspend fun deleteMeal(meal: MealEntity) = dao.deleteMeal(meal)
}
