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

    companion object {
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.afternoon)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.start()

        isRunning = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Background Music",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("Background Music")
            .setContentText("Music is playing in the background")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
