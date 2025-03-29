package com.example.swiftbite.Listeners

import com.example.swiftbite.Models.RandomRecipeApiResponse

interface RandomRecipeResponseListener {
    fun didFetch(response: RandomRecipeApiResponse, message: String)
    fun didError(message: String)
}
