package com.example.swiftbite.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Models.Recipe
import com.example.swiftbite.R
import com.squareup.picasso.Picasso

class RandomRecipeAdapter(private val context: Context, private val list: List<Recipe>) : RecyclerView.Adapter<RandomRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomRecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false)
        return RandomRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RandomRecipeViewHolder, position: Int) {
        holder.textView_title.text = list[position].title
        holder.textView_title.isSelected = true
        holder.textView_likes.text = "${list[position].aggregateLikes} Likes"
        holder.textView_servings.text = "${list[position].servings} Servings"
        holder.textView_time.text = "${list[position].readyInMinutes} Minutes"
        Picasso.get().load(list[position].image).into(holder.imageView_food)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class RandomRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val random_list_container: CardView = itemView.findViewById(R.id.random_list_container)
    val textView_title: TextView = itemView.findViewById(R.id.textView_title)
    val textView_servings: TextView = itemView.findViewById(R.id.textView_servings)
    val textView_likes: TextView = itemView.findViewById(R.id.textView_likes)
    val textView_time: TextView = itemView.findViewById(R.id.textView_time)
    val imageView_food: ImageView = itemView.findViewById(R.id.imageView_food)
}