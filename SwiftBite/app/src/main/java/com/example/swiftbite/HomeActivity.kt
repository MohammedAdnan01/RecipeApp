package com.example.swiftbite

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.widget.ImageView
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

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

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

        // Find the Explore button and set up a click listener
        val exploreButton = findViewById<Button>(R.id.exploreButton)
        exploreButton.setOnClickListener {
            // When the Explore button is clicked, open IngredientsActivity
            val intent = Intent(this, IngredientActivity::class.java)
            startActivity(intent)
        }
        initializeSpeechRecognizer()

        // Set up microphone button to start speech recognition
        val micButton = findViewById<ImageView>(R.id.micButton)
        micButton.setOnClickListener {
            // Start listening to the microphone
            speechRecognizer.startListening(recognizerIntent)
        }
    }

    private fun initializeSpeechRecognizer() {
        try {
            // Check if speech recognition is available
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

                // Set up the recognition listener
                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Other client-side errors"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService is busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                            else -> "Unknown error"
                        }
                        Toast.makeText(this@HomeActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches != null && matches.isNotEmpty()) {
                            val spokenText = matches[0]
                            // Use the recognized text for searching
                            searchRecipes(spokenText)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            } else {
                Toast.makeText(this, "Speech recognition is not available on this device.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing SpeechRecognizer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchRecipes(query: String) {
        tags.clear()
        tags.add(query)
        manager.getRandomRecipes(randomRecipeResponseListener, tags)
        dialog?.show()
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