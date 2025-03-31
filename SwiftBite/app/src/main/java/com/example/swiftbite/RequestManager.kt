package com.example.swiftbite

import android.content.Context
import com.example.swiftbite.Listeners.RandomRecipeResponseListener
import com.example.swiftbite.Listeners.RecipeDetailsListener
import com.example.swiftbite.Models.RandomRecipeApiResponse
import com.example.swiftbite.Models.RecipeDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class RequestManager(private val context: Context) {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getRandomRecipes(listener: RandomRecipeResponseListener, tags: List<String>) {
        val callRandomRecipes = retrofit.create(CallRandomRecipes::class.java)
        val call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key), "10", tags)
        call.enqueue(object : Callback<RandomRecipeApiResponse> {
            override fun onResponse(call: Call<RandomRecipeApiResponse>, response: Response<RandomRecipeApiResponse>) {
                if (!response.isSuccessful) {
                    listener.didError(response.message())
                    return
                }
                response.body()?.let {
                    listener.didFetch(it, response.message())
                } ?: listener.didError("No data received from API")
            }

            override fun onFailure(call: Call<RandomRecipeApiResponse>, t: Throwable) {
                listener.didError(t.message ?: "Unknown error occurred") //listener.didError(t.message)
            }
        })
    }

    fun getRecipeDetails(listener: RecipeDetailsListener, id: Int) {
        val callRecipeDetails = retrofit.create(CallRecipeDetails::class.java)
        val call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_key))
        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (!response.isSuccessful) {
                    listener.didError(response.message())
                    return
                }
                listener.didFetch(response.body()!!, response.message())
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                listener.didError(t.message ?: "Unknown error")
            }
        })
    }

    private interface CallRandomRecipes {
        @GET("recipes/random")
        fun callRandomRecipe(
            @Query("apiKey") apiKey: String,
            @Query("number") number: String,
            @Query("tags") tags: List<String>
        ): Call<RandomRecipeApiResponse>
    }

    private interface CallRecipeDetails {
        @GET("recipes/{id}/information")
        fun callRecipeDetails(
            @Path("id") id: Int,
            @Query("apiKey") apiKey: String
        ): Call<RecipeDetailsResponse>
    }
}