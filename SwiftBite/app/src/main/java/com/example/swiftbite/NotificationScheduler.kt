package com.example.swiftbite

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {
    private const val REQUEST_CODE_12PM = 1
    private const val REQUEST_CODE_4PM = 2
    private const val REQUEST_CODE_9PM = 3

    fun scheduleDailyNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Cancel existing alarms to avoid duplicates
        cancelExistingAlarms(context, alarmManager)
        // Schedule for 12:00 PM
        scheduleNotification(context, alarmManager, 12, 0, REQUEST_CODE_12PM)
        // Schedule for 4:00 PM (16:00 in 24-hour format)
        scheduleNotification(context, alarmManager, 16, 0, REQUEST_CODE_4PM)
        // Schedule for 9:00 PM (21:00 in 24-hour format)
        scheduleNotification(context, alarmManager, 22, 30, REQUEST_CODE_9PM)
    }

    private fun scheduleNotification(
        context: Context,
        alarmManager: AlarmManager,
        hour: Int,
        minute: Int,
        requestCode: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.swiftbite.NOTIFICATION_ACTION"
            putExtra("notification_id", requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelExistingAlarms(context: Context, alarmManager: AlarmManager) {
        val requestCodes = listOf(REQUEST_CODE_12PM, REQUEST_CODE_4PM, REQUEST_CODE_9PM)
        for (requestCode in requestCodes) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = "com.example.swiftbite.NOTIFICATION_ACTION"
                putExtra("notification_id", requestCode)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}