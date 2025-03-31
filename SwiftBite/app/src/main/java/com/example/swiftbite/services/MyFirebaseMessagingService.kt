package com.example.swiftbite.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCM"

    override fun onCreate() {
        super.onCreate()
        // No need to subscribe here anymore; subscription is handled in MainActivity
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message Received: ${remoteMessage.data}")

        // Handle Notification Payload (when the message is a notification)
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "New Message", it.body ?: "Check out the latest update!")
        }

        // Handle Data Payload (when the message contains additional data)
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "New Update"
            val body = remoteMessage.data["body"] ?: "Check out the latest update!"
            sendNotification(title, body)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, body: String) {
        val channelId = "swiftbite_notifications"
        val notificationId = System.currentTimeMillis().toInt()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel (For Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SwiftBite Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new recipe posts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build and show the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Token: $token")
        // Handle token, maybe send it to the server for the user
        subscribeToTopic()
    }

    // Handle topic subscription once (should be done in MainActivity)
    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) {
                    "Successfully subscribed to 'all' topic"
                } else {
                    "Subscription failed"
                }
                Log.d(TAG, msg)
            }
    }
}
