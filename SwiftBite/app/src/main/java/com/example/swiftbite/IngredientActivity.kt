package com.example.swiftbite

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.ads.mediationtestsuite.activities.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class IngredientActivity : AppCompatActivity() {

    private lateinit var ingredientEditText: EditText
    private lateinit var addButton: Button
    private lateinit var ingredientsListLayout: LinearLayout

    private val ingredients = mutableListOf<String>()  // Store ingredients as a list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient)

        ingredientEditText = findViewById(R.id.editTextIngredient)
        addButton = findViewById(R.id.buttonAddIngredient)
        ingredientsListLayout = findViewById(R.id.ingredientsList)

        // Button to add ingredient
        addButton.setOnClickListener {
            val ingredient = ingredientEditText.text.toString().trim()
            if (ingredient.isNotEmpty()) {
                ingredients.add(ingredient)
                updateIngredientsList()
                ingredientEditText.text.clear()
            } else {
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            }
        }

        // Button to recommend dishes
        val recommendButton = findViewById<Button>(R.id.buttonRecommendDishes)
        recommendButton.setOnClickListener {
            // Navigate to the next activity and pass the ingredients list
            val intent = Intent(this, RecommendDishesActivity::class.java)
            intent.putStringArrayListExtra("ingredients", ArrayList(ingredients))  // Passing ingredients list
            startActivity(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)

        bottomNav.menu.findItem(R.id.nav_explore).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    intent = Intent(this, HomeActivity::class.java)
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

    // Function to update the displayed list of ingredients
    private fun updateIngredientsList() {
        ingredientsListLayout.removeAllViews()  // Clear existing views

        for (ingredient in ingredients) {
            val ingredientView = LayoutInflater.from(this).inflate(R.layout.ingredient_item, null)
            val textViewIngredient = ingredientView.findViewById<TextView>(R.id.textViewIngredient)
            val deleteButton = ingredientView.findViewById<Button>(R.id.buttonDeleteIngredient)

            textViewIngredient.text = ingredient
            deleteButton.setOnClickListener {
                removeIngredient(ingredient)  // Remove the ingredient from the list
            }

            ingredientsListLayout.addView(ingredientView)  // Add new ingredient to layout
        }
    }

    // Function to remove an ingredient
    private fun removeIngredient(ingredient: String) {
        ingredients.remove(ingredient)
        updateIngredientsList()  // Refresh the list after removal
    }
}