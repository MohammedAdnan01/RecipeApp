package com.example.swiftbite

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtils.createNotificationChannel(context)
        val notificationId = intent.getIntExtra("notification_id", 0)
        NotificationUtils.showNotification(context, notificationId)
    }
}