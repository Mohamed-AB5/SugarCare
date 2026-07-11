package com.example.sugercare.viewModels

import androidx.lifecycle.ViewModel
import com.sugarcare.app.ui.screens.DoctorMeal
import com.sugarcare.app.ui.screens.LocalMealData
import com.sugarcare.app.ui.screens.MealLog
import com.sugarcare.app.ui.screens.MealSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for the Meal Plan screen.
 *
 * Holds state for all three tabs:
 *   1. Doctor Meal Plan  — loaded from [LocalMealData]
 *   2. Meal Suggestions  — loaded from [LocalMealData], filterable by category
 *   3. Meal Log          — kept in-memory (in-memory list for today's session)
 *
 * ── Firestore upgrade path ──────────────────────────────────────
 * Replace the two LocalMealData calls in [init] with suspend functions
 * that fetch from Firestore, wrapped in viewModelScope.launch { }.
 * The StateFlows and the entire UI stay unchanged.
 * ───────────────────────────────────────────────────────────────
 */
class MealViewModel : ViewModel() {

    // ── Tab 1: Doctor Meal Plan ───────────────────────────────

    private val _doctorMeals = MutableStateFlow<List<DoctorMeal>>(emptyList())
    val doctorMeals: StateFlow<List<DoctorMeal>> = _doctorMeals.asStateFlow()

    // ── Tab 2: Meal Suggestions ───────────────────────────────

    private val _allSuggestions = MutableStateFlow<List<MealSuggestion>>(emptyList())

    /** Currently visible suggestions — filtered by [selectedCategory] */
    private val _filteredSuggestions = MutableStateFlow<List<MealSuggestion>>(emptyList())
    val filteredSuggestions: StateFlow<List<MealSuggestion>> = _filteredSuggestions.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Snack")

    // ── Tab 3: Meal Log ───────────────────────────────────────

    private val _mealLogs = MutableStateFlow<List<MealLog>>(emptyList())
    val mealLogs: StateFlow<List<MealLog>> = _mealLogs.asStateFlow()

    // Tracks which meal type the user is currently logging
    private val _selectedLogType = MutableStateFlow("Breakfast")
    val selectedLogType: StateFlow<String> = _selectedLogType.asStateFlow()

    private val _logNotes = MutableStateFlow("")
    val logNotes: StateFlow<String> = _logNotes.asStateFlow()

    private val _logMealName = MutableStateFlow("")
    val logMealName: StateFlow<String> = _logMealName.asStateFlow()

    // Feedback message after saving a log
    private val _logSaveMessage = MutableStateFlow("")
    val logSaveMessage: StateFlow<String> = _logSaveMessage.asStateFlow()

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // ─────────────────────────────────────────────────────────
    init {
        loadDoctorMeals()
        loadSuggestions()
    }

    // ── Loaders ───────────────────────────────────────────────

    private fun loadDoctorMeals() {
        // Swap this line for a Firestore fetch later
        _doctorMeals.value = LocalMealData.getDoctorMealPlan()
    }

    private fun loadSuggestions() {
        // Swap this line for a Firestore fetch later
        val data = LocalMealData.getMealSuggestions()
        _allSuggestions.value = data
        _filteredSuggestions.value = data
    }

    // ── Tab 2 Actions ─────────────────────────────────────────

    fun filterByCategory(category: String) {
        _selectedCategory.value = category
        _filteredSuggestions.value = if (category == "All") {
            _allSuggestions.value
        } else {
            _allSuggestions.value.filter { it.category == category }
        }
    }

    // ── Tab 3 Actions ─────────────────────────────────────────

    fun updateSelectedLogType(type: String) {
        _selectedLogType.value = type
        // Pre-fill meal name from doctor's plan if available
        _logMealName.value = _doctorMeals.value
            .firstOrNull { it.mealType == type }?.name ?: ""
    }

    fun updateLogNotes(notes: String) {
        _logNotes.value = notes
    }

    fun updateLogMealName(name: String) {
        _logMealName.value = name
    }

    fun saveLog(completed: Boolean) {
        val mealName = _logMealName.value.trim()
        if (mealName.isEmpty()) {
            _logSaveMessage.value = "Please enter a meal name."
            return
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date())

        val newLog = MealLog(
            mealType         = _selectedLogType.value,
            selectedMealName = mealName,
            completed        = completed,
            notes            = _logNotes.value.trim(),
            loggedAt         = timestamp
        )

        // Append or replace existing log for same meal type today
        val existing = _mealLogs.value.toMutableList()
        val idx = existing.indexOfFirst { it.mealType == newLog.mealType }
        if (idx >= 0) existing[idx] = newLog else existing.add(newLog)

        _mealLogs.value = existing
        _logNotes.value = ""
        _logSaveMessage.value = "✓ ${newLog.mealType} logged successfully."
    }

    fun clearLogMessage() {
        _logSaveMessage.value = ""
    }
}
