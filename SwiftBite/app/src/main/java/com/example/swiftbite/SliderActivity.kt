package com.example.swiftbite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.swiftbite.services.BackgroundMusic

class SliderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        val slideLayouts = listOf(
            R.layout.first_time_screen1,
            R.layout.first_time_screen2
        )

        val adapter = ViewPagerAdapter(this, slideLayouts)
        viewPager.adapter = adapter

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < slideLayouts.size - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                startActivity(Intent(this, LoginOptionsActivity::class.java))
                finish()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                nextButton.text = if (position == slideLayouts.size - 1) {
                    "Let's Get Started"
                } else {
                    "Next"
                }
            }
        })
        val musicIntent = Intent(this, BackgroundMusic::class.java)
        startService(musicIntent)
    }
}
