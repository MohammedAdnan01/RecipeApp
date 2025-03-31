package com.example.swiftbite.Models

data class UsedIngredient(
    val id: Int,
    val amount: Double,
    val unit: String,
    val unitLong: String,
    val unitShort: String,
    val aisle: String,
    val name: String,
    val original: String,
    val originalName: String,
    val meta: List<String>,
    val extendedName: String,
    val image: String
)