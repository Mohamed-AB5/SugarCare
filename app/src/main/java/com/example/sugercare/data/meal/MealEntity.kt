package com.example.sugercare.data.meal


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity — maps to the "meals" table in the local SQLite database.
 *
 * ── Firestore upgrade path ──────────────────────────────────────
 * Every field maps 1-to-1 to a Firestore document field.
 * To migrate: read documents from users/{uid}/meals and convert
 * each document snapshot to a MealEntity. No UI changes required.
 * ───────────────────────────────────────────────────────────────
 *
 * @param id          Auto-generated primary key.
 * @param mealName    Name of the meal entered by the user.
 * @param mealType    One of: Breakfast | Lunch | Dinner | Snack.
 * @param description Free-text description of the meal.
 * @param calories    Optional calorie count.
 * @param mealTime    User-selected time string, e.g. "08:30 AM".
 * @param createdAt   Unix epoch millis — set automatically on insert.
 */
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mealName: String,
    val mealType: String,
    val description: String,
    val calories: Int?,
    val mealTime: String,
    val createdAt: Long = System.currentTimeMillis()
)
