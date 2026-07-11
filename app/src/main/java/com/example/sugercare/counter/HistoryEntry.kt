package com.example.sugercare.counter

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class HistoryEntry(
    val id        : String = UUID.randomUUID().toString(),
    val action    : String,
    val date      : Long,
    val startDate : Long,
    val totalDays : Int
)
