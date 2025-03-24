package com.example.swiftbite

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class SliderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to your ViewPager2 layout
        setContentView(R.layout.activity_slider)

        // Set up the ViewPager2
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // List of slide layouts (you can add as many as needed)
        val slideLayouts = listOf(
            R.layout.first_time_screen1,  // First slide layout
            R.layout.first_time_screen2,  // Second slide layout
            // Third slide layout
        )

        // Set up the adapter for the ViewPager2
        val adapter = ViewPagerAdapter(slideLayouts)
        viewPager.adapter = adapter

        // Optional: Add functionality to the "Next" button
        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < slideLayouts.size - 1) {
                // Move to the next slide
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                // If it's the last slide, finish the activity or navigate elsewhere
                finish()  // Or use an Intent to start another activity, like the main screen
            }
        }

        // Listen for page changes to change the button text
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val nextButton: Button = findViewById(R.id.nextButton)
                // Change button text based on the slide position
                if (position == 1) {
                    nextButton.text = "Let's Get Started"
                } else {
                    nextButton.text = "Next"
                }
            }
        })
    }

    // Adapter to provide the layouts for each page of the ViewPager2
    class ViewPagerAdapter(private val layouts: List<Int>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PagerViewHolder {
            val view = android.view.LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return PagerViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return layouts[position]
        }

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            // Bind data if needed
        }

        override fun getItemCount(): Int {
            return layouts.size
        }

        class PagerViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
    }
}
