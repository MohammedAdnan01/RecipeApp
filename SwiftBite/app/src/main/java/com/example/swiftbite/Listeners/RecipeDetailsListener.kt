package com.example.swiftbite.Listeners

import com.example.swiftbite.Models.RecipeDetailsResponse

interface RecipeDetailsListener {
    fun didFetch(response: RecipeDetailsResponse, message: String)
    fun didError(message: String)
}