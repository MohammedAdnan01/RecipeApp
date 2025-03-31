package com.example.swiftbite

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        auth = FirebaseAuth.getInstance()

        val signOutButton = findViewById<Button>(R.id.btn_sign_out)
        signOutButton.setOnClickListener {
            signOut()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)

        bottomNav.menu.findItem(R.id.nav_setting).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent, options.toBundle())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_explore -> {
                    val intent = Intent(this, IngredientActivity::class.java)
                    startActivity(intent)
                    startActivity(intent, options.toBundle())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_setting -> {
                    return@setOnItemSelectedListener true // Already in this activity
                }
            }
            false
        }
    }

    private fun signOut() {
        auth.signOut()  // Sign out from Firebase
        // Navigate back to LoginActivity after sign-out
        val intent = Intent(this, LoginOptionsActivity::class.java)
        startActivity(intent)
        finish()  // Optional: close the current activity
    }
}