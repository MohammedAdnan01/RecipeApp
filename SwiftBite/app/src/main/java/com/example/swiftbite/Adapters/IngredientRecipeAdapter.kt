package com.example.swiftbite.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Models.IngredientBasedRecipe
import com.example.swiftbite.R
import com.example.swiftbite.RecipeDetailsActivity
import com.squareup.picasso.Picasso

class IngredientRecipeAdapter(
    private val context: Context,
    private val list: List<IngredientBasedRecipe>
) : RecyclerView.Adapter<IngredientRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientRecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false)
        return IngredientRecipeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: IngredientRecipeViewHolder, position: Int) {
        val recipe = list[position]
        holder.textView_title.text = recipe.title
        holder.textView_title.isSelected = true
        holder.textView_servings.text = "${recipe.usedIngredientCount} Used Ingredients"
        holder.textView_time.text = "${recipe.missedIngredientCount} Missing Ingredients"
        Picasso.get().load(recipe.image).into(holder.imageView_food)

        // Add click listener to the item view
        holder.random_list_container.setOnClickListener {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("id", recipe.id.toString()) // Pass the recipe ID
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class IngredientRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val random_list_container: CardView = itemView.findViewById(R.id.random_list_container)
    val textView_title: TextView = itemView.findViewById(R.id.textView_title)
    val textView_servings: TextView = itemView.findViewById(R.id.textView_servings)
    val textView_time: TextView = itemView.findViewById(R.id.textView_time)
    val imageView_food: ImageView = itemView.findViewById(R.id.imageView_food)
}