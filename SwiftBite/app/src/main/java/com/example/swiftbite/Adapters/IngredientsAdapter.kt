package com.example.swiftbite.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Models.ExtendedIngredient
import com.example.swiftbite.R
import com.squareup.picasso.Picasso

class IngredientsAdapter(
    private val context: Context,
    private val list: List<ExtendedIngredient>
) : RecyclerView.Adapter<IngredientsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_meal_ingredients, parent, false)
        return IngredientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.textViewIngredientsName.text = list[position].name
        holder.textViewIngredientsName.isSelected = true
        holder.textViewIngredientsQuantity.text = list[position].original
        holder.textViewIngredientsQuantity.isSelected = true
        Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/${list[position].image}").into(holder.imageViewIngredients)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class IngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewIngredientsQuantity: TextView = itemView.findViewById(R.id.textView_ingredients_quantity)
    val textViewIngredientsName: TextView = itemView.findViewById(R.id.textView_ingredients_name)
    val imageViewIngredients: ImageView = itemView.findViewById(R.id.imageView_ingredients)
}

