package com.example.sugercare.data.meal


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Singleton Room database for the SugarCare meal feature.
 *
 * Version history
 * ───────────────
 * 1 — initial schema: meals table
 *
 * When adding new entities or changing columns, increment [version]
 * and provide a Migration object, or use fallbackToDestructiveMigration()
 * during development only.
 */
@Database(
    entities  = [MealEntity::class],
    version   = 1,
    exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao

    companion object {

        @Volatile
        private var INSTANCE: MealDatabase? = null

        /**
         * Returns the singleton instance, creating it if necessary.
         * Thread-safe via double-checked locking.
         */
        fun getInstance(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "sugar_care_meal_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
