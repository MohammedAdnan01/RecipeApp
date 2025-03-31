package com.example.swiftbite

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Adapters.IngredientsAdapter
import com.example.swiftbite.Listeners.RecipeDetailsListener
import com.example.swiftbite.Models.RecipeDetailsResponse
import com.squareup.picasso.Picasso

class RecipeDetailsActivity : AppCompatActivity() {
    private var id: Int = 0
    private lateinit var textView_meal_name: TextView
    private lateinit var textView_meal_source: TextView
    private lateinit var textView_meal_summary: TextView
    private lateinit var imageView_meal_image: ImageView
    private lateinit var recycler_meal_ingredients: RecyclerView
    private lateinit var ingredientsAdapter : IngredientsAdapter
    private lateinit var manager: RequestManager
    private lateinit var dialog: ProgressDialog

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
    }

    private fun findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name)
        textView_meal_source = findViewById(R.id.textView_meal_source)
        textView_meal_summary = findViewById(R.id.textView_meal_summary)
        imageView_meal_image = findViewById(R.id.imageView_meal_image)
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients)
    }

    private val recipeDetailsListener = object : RecipeDetailsListener {
        override fun didFetch(response: RecipeDetailsResponse, message: String) {
            dialog.dismiss()
            textView_meal_name.text = response.title
            textView_meal_source.text = response.sourceName
            textView_meal_summary.text = response.summary
            Picasso.get().load(response.image).into(imageView_meal_image)

            recycler_meal_ingredients.setHasFixedSize(true)
            recycler_meal_ingredients.layoutManager = LinearLayoutManager(this@RecipeDetailsActivity,LinearLayoutManager.HORIZONTAL,false)
            ingredientsAdapter = IngredientsAdapter(this@RecipeDetailsActivity, response.extendedIngredients)
            recycler_meal_ingredients.adapter = ingredientsAdapter
        }

        override fun didError(message: String) {
            Toast.makeText(this@RecipeDetailsActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}