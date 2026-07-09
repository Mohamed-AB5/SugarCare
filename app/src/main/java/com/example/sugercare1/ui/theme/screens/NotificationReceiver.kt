package com.sugarcare.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medName = intent.getStringExtra("med_name") ?: "Medication"
        
        val builder = NotificationCompat.Builder(context, "sugar_care_channel")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("SugarCare Reminder")
            .setContentText("Time to take your: $medName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            
            notify(medName.hashCode(), builder.build())
        }
    }
}
