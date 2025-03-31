package com.example.swiftbite


import android.app.Application
import android.content.Intent
import com.example.swiftbite.services.BackgroundMusic

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val musicIntent = Intent(this, BackgroundMusic::class.java)
        startService(musicIntent) // Start music service when app starts
    }
}
