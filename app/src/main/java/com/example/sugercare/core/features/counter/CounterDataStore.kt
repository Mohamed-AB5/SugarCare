package com.example.sugercare.core.features.counter


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.counterDataStore by preferencesDataStore("counter_prefs")
class CounterDataStore(private val context: Context) {

    companion object {
        fun startDateKey(uid: String)  = longPreferencesKey("start_date_$uid")
        fun totalDaysKey(uid: String)  = intPreferencesKey("total_days_$uid")
        fun isRunningKey(uid: String)  = booleanPreferencesKey("is_running_$uid")
        fun bestStreakKey(uid: String) = intPreferencesKey("best_streak_$uid")
        fun historyKey(uid: String)    = stringPreferencesKey("history_json_$uid")
    }

    fun startDateFlow(uid: String): Flow<Long> =
        context.counterDataStore.data.map { it[startDateKey(uid)] ?: System.currentTimeMillis() }

    fun totalDaysFlow(uid: String): Flow<Int> =
        context.counterDataStore.data.map { it[totalDaysKey(uid)] ?: 90 }

    fun isRunningFlow(uid: String): Flow<Boolean> =
        context.counterDataStore.data.map { it[isRunningKey(uid)] ?: false }

    fun bestStreakFlow(uid: String): Flow<Int> =
        context.counterDataStore.data.map { it[bestStreakKey(uid)] ?: 0 }

    fun historyFlow(uid: String): Flow<String> =
        context.counterDataStore.data.map { it[historyKey(uid)] ?: "[]" }

    suspend fun saveStartDate(uid: String, date: Long) {
        context.counterDataStore.edit { it[startDateKey(uid)] = date }
    }

    suspend fun saveTotalDays(uid: String, days: Int) {
        context.counterDataStore.edit { it[totalDaysKey(uid)] = days }
    }

    suspend fun saveIsRunning(uid: String, running: Boolean) {
        context.counterDataStore.edit { it[isRunningKey(uid)] = running }
    }

    suspend fun saveBestStreak(uid: String, streak: Int) {
        context.counterDataStore.edit { it[bestStreakKey(uid)] = streak }
    }

    suspend fun saveHistory(uid: String, json: String) {
        context.counterDataStore.edit { it[historyKey(uid)] = json }
    }
}