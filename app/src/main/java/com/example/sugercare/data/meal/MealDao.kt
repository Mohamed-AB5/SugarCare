package com.example.sugercare.data.meal


import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the meals table.
 *
 * All queries return Flow so the UI reacts automatically
 * whenever the database changes — no manual refresh needed.
 */
@Dao
interface MealDao {

    // ── Read ──────────────────────────────────────────────────

    /** Emits the full meal list ordered newest-first whenever it changes. */
    @Query("SELECT * FROM meals ORDER BY createdAt DESC")
    fun getAllMeals(): Flow<List<MealEntity>>

    /** Emits meals filtered by type, ordered newest-first. */
    @Query("SELECT * FROM meals WHERE mealType = :type ORDER BY createdAt DESC")
    fun getMealsByType(type: String): Flow<List<MealEntity>>

    /** Emits meals whose name contains the search query (case-insensitive). */
    @Query("SELECT * FROM meals WHERE mealName LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchMeals(query: String): Flow<List<MealEntity>>

    // ── Write ─────────────────────────────────────────────────

    /** Inserts a new meal. Room auto-generates the id. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    /** Replaces the existing row that matches the entity's id. */
    @Update
    suspend fun updateMeal(meal: MealEntity)

    /** Deletes the row matching the entity's id. */
    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}
