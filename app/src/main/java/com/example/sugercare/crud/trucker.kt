package com.example.sugercare.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.app.SugarReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val savedReadingsList = MutableStateFlow<List<SugarReading>>(emptyList())
class SugarTrackerViewModel : ViewModel() {
    private val _trackerState = MutableStateFlow<TrackerState>(TrackerState.Idle)
    val trackerState: StateFlow<TrackerState> = _trackerState.asStateFlow()
    val readingsList: StateFlow<List<SugarReading>> = savedReadingsList.asStateFlow()
    private val _glucoseInput = MutableStateFlow("")
    val glucoseInput: StateFlow<String> = _glucoseInput.asStateFlow()
    private val _mealRelation = MutableStateFlow("")
    val mealRelation: StateFlow<String> = _mealRelation.asStateFlow()
    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()
    private val _hourInput = MutableStateFlow("")
    val hourInput: StateFlow<String> = _hourInput.asStateFlow()
    private val _minuteInput = MutableStateFlow("")
    val minuteInput: StateFlow<String> = _minuteInput.asStateFlow()
    private val _amPmSelection = MutableStateFlow("AM")
    val amPmSelection: StateFlow<String> = _amPmSelection.asStateFlow()

    fun updateGlucoseInput(newValue: String) { _glucoseInput.value = newValue }
    fun updateMealRelation(newValue: String) { _mealRelation.value = newValue }
    fun updateNote(newValue: String) { _note.value = newValue }
    fun updateHourInput(newValue: String) { _hourInput.value = newValue }
    fun updateMinuteInput(newValue: String) { _minuteInput.value = newValue }
    fun updateAmPmSelection(newValue: String) { _amPmSelection.value = newValue }
    fun resetTrackerState() { _trackerState.value = TrackerState.Idle }
    fun saveSugarRecord() {
        val glucose = _glucoseInput.value
        val meal = _mealRelation.value
        val hour = _hourInput.value
        val minute = _minuteInput.value
        val amPm = _amPmSelection.value

        when {
            glucose.isEmpty() -> { _trackerState.value = TrackerState.Error("Glucose reading is required") }
            meal.isEmpty() -> { _trackerState.value = TrackerState.Error("Meal relation is required") }
            hour.isEmpty() || minute.isEmpty() -> {
                _trackerState.value = TrackerState.Error("Time is required")
            }
            else -> {
                val level = glucose.toIntOrNull() ?: 0
                if (level > 0) {
                    val detailsBuilder = mutableListOf<String>()
                    detailsBuilder.add("Meal: $meal")
                    if (_note.value.isNotEmpty()) detailsBuilder.add("Note: ${_note.value}")
                    detailsBuilder.add("Time: $hour:$minute $amPm")

                    val fullDetails = detailsBuilder.joinToString(" | ")
                    performSaveRecord(level, fullDetails)
                } else {
                    _trackerState.value = TrackerState.Error("Invalid glucose level")
                }
            }
        }
    }
    private fun performSaveRecord(level: Int, details: String) {
        _trackerState.value = TrackerState.Loading
        viewModelScope.launch {
            try {
                val newReading = SugarReading(
                    glucoseLevel = level,
                    note = details
                )
                savedReadingsList.value = savedReadingsList.value + newReading
                _trackerState.value = TrackerState.Success
                clearForm()
            } catch (e: Exception) {
                _trackerState.value = TrackerState.Error(e.message ?: "Save failed")
            }
        }
    }
    fun deleteReading(id: String) {
        savedReadingsList.value = savedReadingsList.value.filter { it.id != id }
    }
    private fun clearForm() {
        _glucoseInput.value = ""
        _mealRelation.value = ""
        _note.value = ""
        _hourInput.value = ""
        _minuteInput.value = ""
        _amPmSelection.value = "AM"
    }
}
sealed class TrackerState {
    object Success : TrackerState()
    object Idle : TrackerState()
    object Loading : TrackerState()
    data class Error(val message: String) : TrackerState()
}