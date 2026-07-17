package com.example.sugercare.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver that fires when an AlarmManager alarm triggers.
 *
 * Receives the medication name and notification ID as Intent extras,
 * then delegates to [NotificationHelper] to post the notification.
 *
 * ── BootReceiver note ───────────────────────────────────────────
 * AlarmManager alarms are cleared when the device reboots.
 * To reschedule them automatically after reboot:
 *   1. Create a BootReceiver that listens for ACTION_BOOT_COMPLETED.
 *   2. In its onReceive(), reload the saved medication list and call
 *      ReminderScheduler.scheduleDailyReminder() for each one.
 *   3. Add RECEIVE_BOOT_COMPLETED permission to the manifest.
 * This is intentionally left out for now to keep the project lightweight.
 * ───────────────────────────────────────────────────────────────
 */
class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicationName   = intent.getStringExtra(EXTRA_MEDICATION_NAME) ?: return
        val notificationId   = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

        // Ensure the channel exists before posting
        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showMedicationNotification(
            context        = context,
            medicationName = medicationName,
            notificationId = notificationId
        )
    }

    companion object {
        const val EXTRA_MEDICATION_NAME  = "extra_medication_name"
        const val EXTRA_NOTIFICATION_ID  = "extra_notification_id"
    }
}

