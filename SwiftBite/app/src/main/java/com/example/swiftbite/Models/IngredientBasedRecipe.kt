package com.example.swiftbite.Models

data class IngredientBasedRecipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val missedIngredients: List<MissedIngredient>,
    val usedIngredients: List<UsedIngredient>,
    val unusedIngredients: List<Any>,
    val likes: Int
)