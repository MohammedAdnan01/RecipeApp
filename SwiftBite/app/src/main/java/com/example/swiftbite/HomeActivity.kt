package com.example.swiftbite

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Adapters.RandomRecipeAdapter
import com.example.swiftbite.Listeners.RandomRecipeResponseListener
import com.example.swiftbite.Models.RandomRecipeApiResponse

class HomeActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null
    private lateinit var manager: RequestManager
    private lateinit var randomRecipeAdapter: RandomRecipeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the home screen
        setContentView(R.layout.home_screen)

        showLoadingDialog()

        // Initialize and fetch API data
        manager = RequestManager(this)
        manager.getRandomRecipes(randomRecipeResponseListener)

        // Simulating delay for dismissing loading dialog
        window.decorView.postDelayed({ dismissLoadingDialog() }, 3000)
    }

    private fun showLoadingDialog() {
        val progressBar = ProgressBar(this)
        val builder = AlertDialog.Builder(this)
            .setView(progressBar)
            .setCancelable(false)

        dialog = builder.create()
        dialog?.show()
    }

    private fun dismissLoadingDialog() {
        dialog?.dismiss()
    }

    private val randomRecipeResponseListener = object : RandomRecipeResponseListener {
        override fun didFetch(response: RandomRecipeApiResponse, message: String) {
            recyclerView = findViewById(R.id.recycler_random)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 1)
            randomRecipeAdapter = RandomRecipeAdapter(this@HomeActivity, response.recipes)
            recyclerView.adapter = randomRecipeAdapter
        }

        override fun didError(message: String) {
            Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}
