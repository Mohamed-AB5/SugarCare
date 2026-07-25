package com.sugarcare.app.ui.screens

/**
 * Represents a single medication with an optional daily reminder time.
 *
 * Replaces the old stub: data class Medication(val name: String, var taken: Boolean)
 *
 * ── Firestore upgrade path ──────────────────────────────────────
 * Each field maps 1-to-1 with a Firestore document field.
 * When connecting Firestore, load a List<MedicationItem> from
 * users/{uid}/medications and push it into the ViewModel StateFlow.
 * This class requires no changes.
 * ───────────────────────────────────────────────────────────────
 *
 * @param id          Unique identifier — used as the AlarmManager request code.
 *                    Generated from System.currentTimeMillis().toInt() at creation time.
 * @param name        Medication name entered by the user.
 * @param taken       Whether the user has marked this dose as taken today.
 * @param reminderHour   24-hour clock hour for the daily reminder (0–23).
 * @param reminderMinute Minute for the daily reminder (0–59).
 * @param reminderLabel  Human-readable time string shown in the UI, e.g. "08:30 AM".
 */
data class MedicationItem(
    val id: Int,
    val name: String,
    val taken: Boolean = false,
    val reminderHour: Int,
    val reminderMinute: Int,
    val reminderLabel: String      // e.g. "08:30 AM"
)
