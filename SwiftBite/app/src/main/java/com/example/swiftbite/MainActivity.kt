package com.example.swiftbite

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.swiftbite.services.BackgroundMusic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to your welcome screen
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("SwiftBitePrefs", MODE_PRIVATE)

        // Check if it's the first time opening the app
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        val user = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isFirstTime -> {
                    // First-time user → Show SliderActivity
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                    startActivity(Intent(this, SliderActivity::class.java))
                }
                user != null -> {
                    // User is already logged in → Go to HomeActivity
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                else -> {
                    // No user logged in → Go to LoginActivity
                    startActivity(Intent(this, LoginOptionsActivity::class.java))
                }
            }
            finish() // Close MainActivity so it's not in the back stack
        }, 2000) // 2 seconds delay

        // Start background music service
        val musicIntent = Intent(this, BackgroundMusic::class.java)
        startService(musicIntent)

        // Subscribe to Firebase Messaging Topic
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "Subscribed to all" else "Subscription failed"
                Log.d("FCM", msg)
            }
    }
}