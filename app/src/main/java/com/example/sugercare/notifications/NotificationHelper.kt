package com.example.sugercare.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.sugarcare.app.R
import android.app.PendingIntent
import android.content.Intent
import com.example.sugercare.MainActivity
/**
 * Handles notification channel creation and notification building.
 *
 * Called from [MedicationReminderReceiver] when an alarm fires.
 */
object NotificationHelper {

    const val CHANNEL_ID   = "medication_reminder_channel"
    const val CHANNEL_NAME = "Medication Reminders"

    /**
     * Creates the notification channel.
     * Safe to call multiple times — Android ignores duplicate registrations.
     * Call this once from MainActivity.onCreate() or from the receiver.
     */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Daily reminders to take your medications"
            enableVibration(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.createNotificationChannel(channel)

    }

    /**
     * Builds and posts the medication reminder notification.
     *
     * @param context        Application context passed in from the receiver.
     * @param medicationName The name of the medication to remind about.
     * @param notificationId Unique ID so multiple medications show separately.
     */
    fun showMedicationNotification(
        context: Context,
        medicationName: String,
        notificationId: Int
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val openIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_screen", "medications")
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💊 Time to take $medicationName")
            .setContentText("Don't forget your medication.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId, notification)
    }
}
