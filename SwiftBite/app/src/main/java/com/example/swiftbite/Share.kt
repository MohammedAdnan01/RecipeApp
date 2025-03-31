package com.example.swiftbite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Share: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)
        // Find the explore button by its ID
        val exploreButton: Button = findViewById(R.id.btn_share)
        // Set up an onClickListener for the explore button
        exploreButton.setOnClickListener {
            // Trigger the share action when clicked
            shareRecipe("🍕 Check out this amazing pizza recipe!")
        }
    }
    // Function to handle sharing the recipe
    private fun shareRecipe(recipeText: String) {
        // Create an Intent to share the recipe
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // Specifies that we're sharing plain text
            putExtra(Intent.EXTRA_TEXT, recipeText) // Add the recipe text as the extra
        }
        // Show the system's share dialog so the user can choose a platform to share on
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}