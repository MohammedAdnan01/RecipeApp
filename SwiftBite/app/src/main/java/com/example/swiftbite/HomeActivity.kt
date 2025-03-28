package com.example.swiftbite

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView // Updated import
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftbite.Adapters.RandomRecipeAdapter
import com.example.swiftbite.Listeners.RandomRecipeResponseListener
import com.example.swiftbite.Models.RandomRecipeApiResponse
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null
    private lateinit var manager: RequestManager
    private lateinit var randomRecipeAdapter: RandomRecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private val tags = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the home screen
        setContentView(R.layout.home_screen)

        showLoadingDialog()

        // Initialize and fetch API data
        manager = RequestManager(this)

        // Load tags from strings.xml
        val tagsArray = resources.getStringArray(R.array.tags)
        tags.addAll(tagsArray)

        // Make the initial API call with the tags
        manager.getRandomRecipes(randomRecipeResponseListener, tags)

        // Initialize search view
        val searchView = findViewById<SearchView>(R.id.searchView_home)
        if (searchView == null) {
            Toast.makeText(this, "SearchView not found in layout", Toast.LENGTH_LONG).show()
            return
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                tags.clear()
                if (query != null) {
                    tags.add(query)
                    manager.getRandomRecipes(randomRecipeResponseListener, tags)
                    dialog?.show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNav == null) {
            Toast.makeText(this, "BottomNavigationView not found in layout", Toast.LENGTH_LONG).show()
            return
        }

        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)

        bottomNav.menu.findItem(R.id.nav_home).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    return@setOnItemSelectedListener true // Already in this Activity
                }
                R.id.nav_explore -> {
                    val intent = Intent(this, SocialMediaActivity::class.java)
                    startActivity(intent, options.toBundle())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent, options.toBundle())
                    return@setOnItemSelectedListener true
                }
            }
            false
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

    private fun dismissLoadingDialog() {
        dialog?.dismiss()
    }

    private val randomRecipeResponseListener = object : RandomRecipeResponseListener {
        override fun didFetch(response: RandomRecipeApiResponse, message: String) {
            dialog?.dismiss()
            recyclerView = findViewById(R.id.recycler_random)
            if (recyclerView == null) {
                Toast.makeText(this@HomeActivity, "RecyclerView not found in layout", Toast.LENGTH_LONG).show()
                return
            }
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 1)
            randomRecipeAdapter = RandomRecipeAdapter(this@HomeActivity, response.recipes ?: emptyList())
            recyclerView.adapter = randomRecipeAdapter
        }

        override fun didError(message: String) {
            dialog?.dismiss()
            Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}