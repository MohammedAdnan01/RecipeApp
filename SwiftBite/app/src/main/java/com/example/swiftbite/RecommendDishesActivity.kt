package com.example.swiftbite

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Adapters.IngredientRecipeAdapter
import com.example.swiftbite.Listeners.IngredientRecipeResponseListener
import com.example.swiftbite.Models.IngredientBasedRecipe
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecommendDishesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestManager: RequestManager
    private var ingredients: List<String> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended_dishes)

        recyclerView = findViewById(R.id.recycler_recommended_dishes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve ingredients passed from IngredientActivity
        ingredients = intent.getStringArrayListExtra("ingredients") ?: mutableListOf()

        requestManager = RequestManager(this)
        fetchRecipesBasedOnIngredients()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)

        bottomNav.menu.findItem(R.id.nav_explore).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent, options.toBundle())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_explore -> {
                    return@setOnItemSelectedListener true // Already in this activity
                }
                R.id.nav_setting -> {
                    intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent, options.toBundle())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun fetchRecipesBasedOnIngredients() {
        if (ingredients.isNotEmpty()) {
            // Use the new method to fetch recipes based on the ingredients
            requestManager.getRecipesByIngredients(object : IngredientRecipeResponseListener {
                override fun didFetch(recipes: List<IngredientBasedRecipe>, message: String) {
                    // Populate the RecyclerView with fetched recipes
                    val adapter = IngredientRecipeAdapter(this@RecommendDishesActivity, recipes)
                    recyclerView.adapter = adapter
                }

                override fun didError(message: String) {
                    // Show error message if the API call fails
                    Toast.makeText(this@RecommendDishesActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }, ingredients) // Pass ingredients to the new method
        } else {
            Toast.makeText(this, "No ingredients provided", Toast.LENGTH_SHORT).show()
        }
    }
}
