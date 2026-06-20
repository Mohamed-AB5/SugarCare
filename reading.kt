package com.example.sugercare.app

import java.util.UUID

data class SugarReading(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val glucoseLevel: Int = 0,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)