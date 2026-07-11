package com.example.sugercare.dataStoreRepo


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.counterDataStore by preferencesDataStore("counter_prefs")

class CounterDataStore(private val context: Context) {
    companion object {
        val KEY_START_DATE  = longPreferencesKey("start_date")
        val KEY_TOTAL_DAYS  = intPreferencesKey("total_days")
        val KEY_IS_RUNNING  = booleanPreferencesKey("is_running")
        val KEY_BEST_STREAK = intPreferencesKey("best_streak")
        val KEY_HISTORY     = stringPreferencesKey("history_json")
    }

    val startDateFlow: Flow<Long>     = context.counterDataStore.data.map { it[KEY_START_DATE] ?: System.currentTimeMillis() }
    val totalDaysFlow: Flow<Int>      =  context.counterDataStore.data.map { it[KEY_TOTAL_DAYS] ?: 90 }
    val isRunningFlow: Flow<Boolean>  = context.counterDataStore.data.map { it[KEY_IS_RUNNING] ?: false }
    val bestStreakFlow: Flow<Int>     = context.counterDataStore.data.map { it[KEY_BEST_STREAK] ?: 0 }
    val historyJsonFlow: Flow<String> = context.counterDataStore.data.map { it[KEY_HISTORY] ?: "[]" }

    suspend fun saveStartDate(date: Long) { context.counterDataStore.edit { it[KEY_START_DATE] = date } }
    suspend fun saveIsRunning(running: Boolean) { context.counterDataStore.edit { it[KEY_IS_RUNNING] = running } }
    suspend fun saveBestStreak(streak: Int) { context.counterDataStore.edit { it[KEY_BEST_STREAK] = streak } }
    suspend fun saveHistory(json: String) { context.counterDataStore.edit { it[KEY_HISTORY] = json } }
}