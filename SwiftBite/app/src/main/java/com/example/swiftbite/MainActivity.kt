package com.example.swiftbite

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.swiftbite.services.BackgroundMusic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.provider.Settings

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to your welcome screen
        setContentView(R.layout.activity_main)

        requestNotificationPermission()
        requestExactAlarmPermission(this)
        requestIgnoreBatteryOptimizations(this)

        NotificationUtils.createNotificationChannel(this)
        NotificationUtils.showNotification(this, 999)
        NotificationScheduler.scheduleDailyNotifications(this)

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
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        }
    }

    private fun requestIgnoreBatteryOptimizations(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Permission denied; you might want to inform the user
        }
    }
}