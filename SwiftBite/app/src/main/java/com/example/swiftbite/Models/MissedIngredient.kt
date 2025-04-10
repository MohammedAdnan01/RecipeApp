package com.example.swiftbite.Models

data class MissedIngredient(
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
    val image: String
)