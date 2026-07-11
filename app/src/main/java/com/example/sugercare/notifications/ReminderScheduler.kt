package com.example.sugercare.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * Schedules and cancels daily medication reminder alarms using AlarmManager.
 *
 * Uses setRepeating() with ELAPSED_REALTIME_WAKEUP — inexact but sufficient
 * for daily medication reminders. No special permissions are required.
 *
 * ── Why not setExact / setExactAndAllowWhileIdle? ───────────────
 * Exact alarms require SCHEDULE_EXACT_ALARM permission (Android 12+)
 * and USE_EXACT_ALARM (Android 13+). For a daily medication reminder
 * a few minutes of drift is acceptable, so inexact repeating alarms
 * are the correct and permission-free choice here.
 *
 * ── Firestore upgrade path ──────────────────────────────────────
 * When medications are loaded from Firestore, call
 * scheduleDailyReminder() for each MedicationItem after the Firestore
 * fetch completes. No changes needed to this class.
 * ───────────────────────────────────────────────────────────────
 */
object ReminderScheduler {

    private const val REPEAT_INTERVAL_MS = AlarmManager.INTERVAL_DAY

    /**
     * Schedules a daily repeating alarm for the given medication.
     *
     * If an alarm with the same [medicationId] already exists it is
     * replaced automatically because PendingIntent reuses the same
     * request code.
     *
     * @param context        Application context.
     * @param medicationId   Unique Int ID — used as the PendingIntent request code.
     * @param medicationName Passed to the receiver as an Intent extra.
     * @param hour           Hour of day (0–23) for the reminder.
     * @param minute         Minute (0–59) for the reminder.
     */
    fun scheduleDailyReminder(
        context: Context,
        medicationId: Int,
        medicationName: String,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = buildReceiverIntent(context, medicationId, medicationName)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build a Calendar for the next occurrence of hour:minute
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Inexact repeating — no special permission required
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            REPEAT_INTERVAL_MS,
            pendingIntent
        )
    }

    /**
     * Cancels a previously scheduled alarm for the given medication ID.
     * Call this when the user deletes a medication.
     *
     * @param context      Application context.
     * @param medicationId Must match the ID used when scheduling.
     * @param medicationName Must match the name used when scheduling.
     */
    fun cancelReminder(
        context: Context,
        medicationId: Int,
        medicationName: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = buildReceiverIntent(context, medicationId, medicationName)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // ── Private helpers ───────────────────────────────────────

    private fun buildReceiverIntent(
        context: Context,
        medicationId: Int,
        medicationName: String
    ): Intent = Intent(context, MedicationReminderReceiver::class.java).apply {
        putExtra(MedicationReminderReceiver.EXTRA_MEDICATION_NAME, medicationName)
        putExtra(MedicationReminderReceiver.EXTRA_NOTIFICATION_ID, medicationId)
    }
}
