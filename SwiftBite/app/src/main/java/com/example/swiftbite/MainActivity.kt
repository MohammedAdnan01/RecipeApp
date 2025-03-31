package com.example.swiftbite

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.swiftbite.services.BackgroundMusic

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val TAG = "FCM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to your welcome screen
        setContentView(R.layout.activity_main)

        // Add a delay to show the welcome screen
        Handler(Looper.getMainLooper()).postDelayed({
            // After 2 seconds, transition to the ViewPager2 screen (activity_slider)
            val intent = Intent(this, SliderActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close the welcome screen so the user cannot go back to it
        }, 2000) // 2 seconds delay

        auth = FirebaseAuth.getInstance()

        // Start the background music service
        val musicIntent = Intent(this, BackgroundMusic::class.java)
        startService(musicIntent)

        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to all"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d(TAG, msg)
            }

    }

    fun signOut(view: View) {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Optional: Do not stop the music here, it will continue running in the background.
    }
}
