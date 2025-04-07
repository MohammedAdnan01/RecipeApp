package com.example.swiftbite

import android.app.Application
import android.content.Intent
import com.example.swiftbite.services.BackgroundMusic

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("SwiftBitePrefs", MODE_PRIVATE)
        val isMusicEnabled = prefs.getBoolean("bg_music_enabled", true)

        if (isMusicEnabled) {
            val musicIntent = Intent(this, BackgroundMusic::class.java)
            startService(musicIntent)
        }
    }
}