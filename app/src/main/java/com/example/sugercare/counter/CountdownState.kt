package com.example.sugercare.counter

data class CountdownState(
    val startDate: Long = System.currentTimeMillis(),
    val totalDays: Int = 90,
    val isRunning: Boolean = false,
    val bestStreak: Int = 0,
    val history: List<HistoryEntry> = emptyList()
)
