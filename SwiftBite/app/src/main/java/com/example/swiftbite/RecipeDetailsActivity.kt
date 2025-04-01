package com.example.swiftbite

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Adapters.IngredientsAdapter
import com.example.swiftbite.Listeners.RecipeDetailsListener
import com.example.swiftbite.Models.Nutrient
import com.example.swiftbite.Models.RecipeDetailsResponse
import com.squareup.picasso.Picasso

class RecipeDetailsActivity : AppCompatActivity() {
    private var id: Int = 0
    private lateinit var textView_meal_name: TextView
    private lateinit var textView_meal_source: TextView
    private lateinit var textView_meal_summary: TextView
    private lateinit var imageView_meal_image: ImageView
    private lateinit var recycler_meal_ingredients: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var btnNutritionBreakdown: Button
    private lateinit var manager: RequestManager
    private lateinit var dialog: ProgressDialog
    private var recipeDetails: RecipeDetailsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        findViews()

        id = intent.getStringExtra("id")?.toInt() ?: 0

        manager = RequestManager(this)
        manager.getRecipeDetails(recipeDetailsListener, id)
        dialog = ProgressDialog(this)
        dialog.setTitle("Loading Details...")
        dialog.show()

        val shareButton = findViewById<Button>(R.id.btn_share)
        shareButton.setOnClickListener {
            shareRecipe()
        }
    }

    private fun findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name)
        textView_meal_source = findViewById(R.id.textView_meal_source)
        textView_meal_summary = findViewById(R.id.textView_meal_summary)
        imageView_meal_image = findViewById(R.id.imageView_meal_image)
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients)
        btnNutritionBreakdown = findViewById(R.id.btn_nutrition_breakdown)
    }

    private fun shareRecipe() {
        val title = textView_meal_name.text.toString()
        val source = textView_meal_source.text.toString()
        val summary = textView_meal_summary.text.toString()

        val shareText = "🍽️ Check out this recipe!\n\n$title\nSource: $source\nSummary: $summary"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Recipe via"))
    }

    private val recipeDetailsListener = object : RecipeDetailsListener {
        override fun didFetch(response: RecipeDetailsResponse, message: String) {
            dialog.dismiss()
            recipeDetails = response
            textView_meal_name.text = response.title
            textView_meal_source.text = response.sourceName
            textView_meal_summary.text = response.summary
            Picasso.get().load(response.image).into(imageView_meal_image)

            Log.d("RecipeDetailsActivity", "Passing ingredients to adapter: ${response.extendedIngredients?.size ?: 0}")
            recycler_meal_ingredients.layoutManager = LinearLayoutManager(this@RecipeDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
            ingredientsAdapter = IngredientsAdapter(this@RecipeDetailsActivity, response.extendedIngredients ?: emptyList())
            recycler_meal_ingredients.adapter = ingredientsAdapter

            btnNutritionBreakdown.setOnClickListener {
                showNutritionBreakdownDialog()
            }
        }

        override fun didError(message: String) {
            dialog.dismiss()
            Toast.makeText(this@RecipeDetailsActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun showNutritionBreakdownDialog() {
        val nutrients = recipeDetails?.nutrition?.nutrients ?: emptyList()
        if (nutrients.isEmpty()) {
            Toast.makeText(this, "No nutritional data available", Toast.LENGTH_SHORT).show()
            return
        }

        // Extract key nutrients (Calories, Fat, Carbohydrates, Protein)
        val calories = nutrients.find { it.name == "Calories" }
        val fat = nutrients.find { it.name == "Fat" }
        val carbs = nutrients.find { it.name == "Carbohydrates" }
        val protein = nutrients.find { it.name == "Protein" }

        // Build the message for the dialog
        val message = StringBuilder("Nutritional Breakdown (per serving):\n\n")
        message.append(formatNutrient(calories, "Calories", "kcal"))
        message.append(formatNutrient(fat, "Fat", "g"))
        message.append(formatNutrient(carbs, "Carbohydrates", "g"))
        message.append(formatNutrient(protein, "Protein", "g"))

        // Show the dialog
        AlertDialog.Builder(this)
            .setTitle("Nutritional Breakdown")
            .setMessage(message.toString())
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun formatNutrient(nutrient: Nutrient?, label: String, unit: String): String {
        return if (nutrient != null && nutrient.amount != null && nutrient.unit != null) {
            "$label: ${nutrient.amount} ${nutrient.unit}\n"
        } else {
            "$label: Not available\n"
        }
    }
}