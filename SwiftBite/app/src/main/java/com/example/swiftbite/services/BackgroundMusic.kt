package com.example.swiftbite.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.swiftbite.R

class BackgroundMusic : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        // Create MediaPlayer instance and set up music
        mediaPlayer = MediaPlayer.create(this, R.raw.afternoon)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)

        // Start playing music
        mediaPlayer.start()

        // Set up the notification channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Background Music",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create a notification to run the service in the foreground
        val notification: Notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("Background Music")
            .setContentText("Music is playing in the background")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()

        // Start the service in the foreground
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Return START_STICKY to keep the service running even if the app is closed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop and release the media player
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}