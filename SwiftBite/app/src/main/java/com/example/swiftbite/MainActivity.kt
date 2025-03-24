package com.example.swiftbite

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to your welcome screen
        setContentView(R.layout.activity_main)

        // Add a delay to show the welcome screen
        Handler().postDelayed({
            // After 2 seconds, transition to the ViewPager2 screen (activity_slider)
            val intent = Intent(this, SliderActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close the welcome screen so the user cannot go back to it
        }, 2000) // 2 seconds delay
    }
}
