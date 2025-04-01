package com.example.swiftbite

import android.content.Context
import android.util.Log
import com.example.swiftbite.Listeners.RandomRecipeResponseListener
import com.example.swiftbite.Listeners.RecipeDetailsListener
import com.example.swiftbite.Listeners.IngredientRecipeResponseListener
import com.example.swiftbite.Models.IngredientBasedRecipe
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
                listener.didError(t.message ?: "Unknown error occurred")
            }
        })
    }

    fun getRecipeDetails(listener: RecipeDetailsListener, id: Int) {
        val callRecipeDetails = retrofit.create(CallRecipeDetails::class.java)
        val call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_key), true)
        Log.d("RequestManager", "Fetching details for recipe ID: $id")
        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (!response.isSuccessful) {
                    Log.e("RequestManager", "Failed to fetch details for ID $id: ${response.message()}")
                    listener.didError(response.message())
                    return
                }
                response.body()?.let {
                    Log.d("RequestManager", "Details for ID $id - Ingredients size: ${it.extendedIngredients?.size ?: 0}")
                    Log.d("RequestManager", "Details for ID $id - Instructions size: ${it.analyzedInstructions?.size ?: 0}")
                    listener.didFetch(it, response.message())
                } ?: listener.didError("No data received from API")
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                Log.e("RequestManager", "Failed to fetch details for ID $id: ${t.message}")
                listener.didError(t.message ?: "Unknown error")
            }
        })
    }

    fun getRecipesByIngredients(listener: IngredientRecipeResponseListener, ingredients: List<String>) {
        val callRecipeByIngredients = retrofit.create(CallRecipeByIngredients::class.java)

        val ingredientList = ingredients.joinToString(",")
        Log.d("Request URL", "https://api.spoonacular.com/recipes/findByIngredients?apiKey=${context.getString(R.string.api_key)}&ingredients=$ingredientList&number=10")

        val call = callRecipeByIngredients.callRecipeByIngredients(
            context.getString(R.string.api_key),
            ingredientList,
            "10"
        )

        call.enqueue(object : Callback<List<IngredientBasedRecipe>> {
            override fun onResponse(call: Call<List<IngredientBasedRecipe>>, response: Response<List<IngredientBasedRecipe>>) {
                if (!response.isSuccessful) {
                    Log.e("RequestManager", "Failed to fetch recipes by ingredients: ${response.message()}")
                    listener.didError("Error: ${response.message()}")
                    return
                }
                response.body()?.let {
                    Log.d("RequestManager", "Fetched ${it.size} recipes by ingredients")
                    it.forEach { recipe ->
                        Log.d("RequestManager", "Recipe ID: ${recipe.id}, Title: ${recipe.title}")
                    }
                    listener.didFetch(it, response.message())
                } ?: listener.didError("No data received from API")
            }

            override fun onFailure(call: Call<List<IngredientBasedRecipe>>, t: Throwable) {
                Log.e("RequestManager", "Failed to fetch recipes by ingredients: ${t.message}")
                listener.didError(t.message ?: "Unknown error occurred")
            }
        })
    }

    private interface CallRecipeByIngredients {
        @GET("recipes/findByIngredients")
        fun callRecipeByIngredients(
            @Query("apiKey") apiKey: String,
            @Query("ingredients") ingredients: String,
            @Query("number") number: String
        ): Call<List<IngredientBasedRecipe>>
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
            @Query("apiKey") apiKey: String,
            @Query("includeNutrition") includeNutrition: Boolean = true
        ): Call<RecipeDetailsResponse>
    }
}