package com.example.swiftbite

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

    }

    fun signOut(view: View) {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
