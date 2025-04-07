package com.example.swiftbite

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Adapters.RandomRecipeAdapter
import com.example.swiftbite.Listeners.RandomRecipeResponseListener
import com.example.swiftbite.Listeners.RecipeClickListener
import com.example.swiftbite.Models.RandomRecipeApiResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null
    private lateinit var manager: RequestManager
    private lateinit var randomRecipeAdapter: RandomRecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private val tags = mutableListOf<String>()

    private val REQUEST_CODE_VOICE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        showLoadingDialog()

        manager = RequestManager(this)

        val tagsArray = resources.getStringArray(R.array.tags)
        tags.addAll(tagsArray)
        manager.getRandomRecipes(randomRecipeResponseListener, tags)

        val searchView = findViewById<SearchView>(R.id.searchView_home)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(searchView)

                val searchQuery = query?.trim().orEmpty()
                if (searchQuery.isEmpty()) {
                    Toast.makeText(this@HomeActivity, "Enter a keyword to search", Toast.LENGTH_SHORT).show()
                    return true
                }

                if (!isValidQuery(searchQuery)) {
                    Toast.makeText(this@HomeActivity, "Invalid input. Only letters and spaces allowed.", Toast.LENGTH_SHORT).show()
                    return true
                }

                tags.clear()
                tags.add(searchQuery)
                manager.getRandomRecipes(randomRecipeResponseListener, tags)
                dialog?.show()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)

        bottomNav.menu.findItem(R.id.nav_home).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_explore -> {
                    startActivity(Intent(this, IngredientActivity::class.java), options.toBundle())
                    true
                }
                R.id.nav_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java), options.toBundle())
                    true
                }
                else -> false
            }
        }

        val micButton = findViewById<ImageView>(R.id.voice_search_button)
        micButton.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a dish or ingredient")

            try {
                startActivityForResult(intent, REQUEST_CODE_VOICE)
            } catch (e: Exception) {
                Toast.makeText(this, "Voice not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoadingDialog() {
        val progressBar = ProgressBar(this)
        val builder = AlertDialog.Builder(this)
            .setView(progressBar)
            .setCancelable(false)

        dialog = builder.create()
        dialog?.show()
    }

    private val randomRecipeResponseListener = object : RandomRecipeResponseListener {
        override fun didFetch(response: RandomRecipeApiResponse, message: String) {
            dialog?.dismiss()
            recyclerView = findViewById(R.id.recycler_random)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@HomeActivity, 1)
                randomRecipeAdapter = RandomRecipeAdapter(
                    this@HomeActivity,
                    response.recipes ?: emptyList(),
                    recipeClickListener
                )
                adapter = randomRecipeAdapter
            }
        }

        override fun didError(message: String) {
            dialog?.dismiss()
            Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private val recipeClickListener: RecipeClickListener = object : RecipeClickListener {
        override fun onRecipeClicked(id: String) {
            Log.d("HomeActivity", "Starting RecipeDetailsActivity with id: $id")
            startActivity(Intent(this@HomeActivity, RecipeDetailsActivity::class.java).putExtra("id", id))
        }
    }

    // Validate search input (letters + space only)
    private fun isValidQuery(query: String): Boolean {
        return query.matches("^[a-zA-Z\\s]+$".toRegex())
    }

    // Hide keyboard from SearchView
    private fun hideKeyboard(searchView: SearchView) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_VOICE && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0)?.trim().orEmpty()

            val searchView = findViewById<SearchView>(R.id.searchView_home)
            searchView.setQuery(spokenText, true)  // Triggers onQueryTextSubmit()
        }
    }
}