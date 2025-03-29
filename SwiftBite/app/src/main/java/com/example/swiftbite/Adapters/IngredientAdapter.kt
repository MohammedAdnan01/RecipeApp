package com.example.swiftbite.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.R

class IngredientAdapter(private val context: Context, private val ingredients: MutableList<String>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientText: TextView = view.findViewById(R.id.textViewIngredient)
        val deleteButton: Button = view.findViewById(R.id.buttonDeleteIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientText.text = ingredient

        holder.deleteButton.setOnClickListener {
            ingredients.removeAt(position)
            notifyItemRemoved(position)  // Notify the adapter that an item was removed
        }
    }

    override fun getItemCount(): Int = ingredients.size
}

