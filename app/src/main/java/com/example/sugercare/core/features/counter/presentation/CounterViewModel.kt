package com.example.sugercare.core.features.counter.presentation

import android.app.Application
import android.util.Log
import androidx.compose.ui.text.style.TextDecoration.Companion.combine
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.core.features.counter.CounterDataStore
import com.example.sugercare.core.features.counter.model.CountdownState
import com.example.sugercare.core.features.counter.model.HistoryEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.TimeUnit

class CounterViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = CounterDataStore(application)

    private val _uiState = MutableStateFlow(CountdownState())
    val uiState: StateFlow<CountdownState> = _uiState.asStateFlow()

    val auth = FirebaseAuth.getInstance()

    private val uid: String
        get() = auth.currentUser?.uid ?: "guest"

    init {
        loadState()
    }

    private fun loadState() {
        viewModelScope.launch {
            try {
                combine(
                    prefsRepo.startDateFlow(uid),
                    prefsRepo.totalDaysFlow(uid),
                    prefsRepo.isRunningFlow(uid),
                    prefsRepo.bestStreakFlow(uid),
                    prefsRepo.historyFlow(uid)
                ) { values ->
                    val startDate = values[0] as Long
                    val totalDays = values[1] as Int
                    val isRunning = values[2] as Boolean
                    val bestStreak = values[3] as Int
                    val historyJson = values[4] as String

                    val history = try {
                        Json.decodeFromString<List<HistoryEntry>>(historyJson)
                    } catch (e: Exception) {
                        emptyList()
                    }

                    CountdownState(startDate, totalDays, isRunning, bestStreak, history)
                }.first().also { _uiState.value = it }

            } catch (e: Exception) {
                Log.e("COUNTER", "loadState failed: ${e.message}")
            }
        }
    }

    fun startCounter() {
        val now = System.currentTimeMillis()
        val entry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            action = "START",
            date = now,
            startDate = now,
            totalDays = _uiState.value.totalDays
        )
        val newHistory = _uiState.value.history + entry

        _uiState.update {
            it.copy(startDate = now, isRunning = true, history = newHistory)
        }

        viewModelScope.launch {
            prefsRepo.saveStartDate(uid,now)
            prefsRepo.saveIsRunning(uid,true)
            prefsRepo.saveHistory(uid, Json.encodeToString(newHistory))
        }
    }

    fun resetCounter() {
        val now = System.currentTimeMillis()
        val elapsedDays = TimeUnit.MILLISECONDS.toDays(now - _uiState.value.startDate).toInt()
        val newBest = maxOf(_uiState.value.bestStreak, elapsedDays)

        val entry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            action = "RESET",
            date = now,
            startDate = now,
            totalDays = _uiState.value.totalDays
        )
        val newHistory = _uiState.value.history + entry

        _uiState.value = _uiState.value.copy(
            startDate = now, isRunning = false, bestStreak = newBest, history = newHistory
        )

        viewModelScope.launch {
            prefsRepo.saveStartDate(uid, now)
            prefsRepo.saveIsRunning(uid, false)
            prefsRepo.saveBestStreak(uid, newBest)
            prefsRepo.saveHistory(uid, Json.encodeToString(newHistory))
        }
    }

    fun updateStartDate(newDate: Long) {
        val entry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            action = "SET",
            date = System.currentTimeMillis(),
            startDate = newDate,
            totalDays = 90
        )
        val newHistory = _uiState.value.history + entry

        _uiState.value =
            _uiState.value.copy(startDate = newDate, history = newHistory)

        viewModelScope.launch {
            prefsRepo.saveStartDate(uid, newDate)
            prefsRepo.saveStartDate(uid, newDate)
            prefsRepo.saveHistory(uid, Json.encodeToString(newHistory))
        }
    }

    fun checkAndUpdateBestStreak() {
        if (!_uiState.value.isRunning) return
        val elapsedDays =
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - _uiState.value.startDate)
                .toInt()
        if (elapsedDays > _uiState.value.bestStreak) {
            _uiState.value = _uiState.value.copy(bestStreak = elapsedDays)
            viewModelScope.launch { prefsRepo.saveBestStreak(uid,elapsedDays) }
        }
    }
    fun clearState() {
        _uiState.value = CountdownState()
    }
}

