package com.example.swiftbite.Listeners

import com.example.swiftbite.Models.IngredientBasedRecipe

interface IngredientRecipeResponseListener {
    fun didFetch(recipes: List<IngredientBasedRecipe>, message: String)
    fun didError(message: String)
}