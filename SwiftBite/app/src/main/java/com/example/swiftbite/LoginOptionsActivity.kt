package com.example.swiftbite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_options)

        // Find the Sign-In and Sign-Up buttons by ID
        val signInButton: Button = findViewById(R.id.signInButton)
        val signUpButton: Button = findViewById(R.id.signUpButton)

        // Set onClickListener for the Sign-In button
        signInButton.setOnClickListener {
            // Navigate to the Sign-In activity
            startActivity(Intent(this, LoginActivity::class.java))  // Replace with your actual Sign-In activity
            finish()  // Optionally finish this activity if you don't want to return to it
        }

        // Set onClickListener for the Sign-Up button
        signUpButton.setOnClickListener {
            // Navigate to the Sign-Up activity
            startActivity(Intent(this, RegisterActivity::class.java))  // Replace with your actual Sign-Up activity
            finish()  // Optionally finish this activity if you don't want to return to it
        }
    }
}
