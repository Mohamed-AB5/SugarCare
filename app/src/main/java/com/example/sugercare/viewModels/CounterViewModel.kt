package com.example.sugercare.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.counter.CountdownState
import com.example.sugercare.counter.HistoryEntry
import com.example.sugercare.dataStoreRepo.CounterDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.TimeUnit


class CounterViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = CounterDataStore(application)

    private val _uiState = MutableStateFlow(CountdownState())
    val uiState: StateFlow<CountdownState> = _uiState.asStateFlow()

    init {
        loadState()
    }

    private fun loadState() {
        viewModelScope.launch {
            val startDate = prefsRepo.startDateFlow.first()
            val totalDays = prefsRepo.totalDaysFlow.first()
            val isRunning = prefsRepo.isRunningFlow.first()
            val bestStreak = prefsRepo.bestStreakFlow.first()
            val historyJson = prefsRepo.historyJsonFlow.first()

            val history = try {
                Json.decodeFromString<List<HistoryEntry>>(historyJson)
            } catch (e: Exception) {
                emptyList()
            }

            _uiState.value = CountdownState(startDate, totalDays, isRunning, bestStreak, history)
        }
    }

    private fun persistHistory(history: List<HistoryEntry>) {
        viewModelScope.launch { prefsRepo.saveHistory(Json.encodeToString(history)) }
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
            prefsRepo.saveStartDate(now)
            prefsRepo.saveIsRunning(true)
            persistHistory(newHistory)
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
            prefsRepo.saveStartDate(now)
            prefsRepo.saveIsRunning(false)
            prefsRepo.saveBestStreak(newBest)
            persistHistory(newHistory)
        }
    }

    fun updateStartDate(newDate: Long) {
        val entry = HistoryEntry(
            id= UUID.randomUUID().toString(),
            action = "SET",
            date = System.currentTimeMillis(),
            startDate = newDate,
            totalDays = 90
        )
        val newHistory = _uiState.value.history + entry

        _uiState.value =
            _uiState.value.copy(startDate = newDate, history = newHistory)

        viewModelScope.launch {
            prefsRepo.saveStartDate(newDate)
            persistHistory(newHistory)
        }
    }

    fun checkAndUpdateBestStreak() {
        if (!_uiState.value.isRunning) return
        val elapsedDays =
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - _uiState.value.startDate)
                .toInt()
        if (elapsedDays > _uiState.value.bestStreak) {
            _uiState.value = _uiState.value.copy(bestStreak = elapsedDays)
            viewModelScope.launch { prefsRepo.saveBestStreak(elapsedDays) }
        }
    }
}