package com.example.swiftbite

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
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
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                tags.clear()
                query?.let {
                    tags.add(it)
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
        bottomNav?.apply {
            menu.findItem(R.id.nav_home).isChecked = true
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        return@setOnItemSelectedListener true // Already in this Activity
                    }
                    R.id.nav_explore -> {
                        val intent = Intent(this@HomeActivity, SocialMediaActivity::class.java)
                        startActivity(intent, ActivityOptions.makeCustomAnimation(this@HomeActivity, 0, 0).toBundle())
                        return@setOnItemSelectedListener true
                    }
                    R.id.nav_setting -> {
                        val intent = Intent(this@HomeActivity, SettingActivity::class.java)
                        startActivity(intent, ActivityOptions.makeCustomAnimation(this@HomeActivity, 0, 0).toBundle())
                        return@setOnItemSelectedListener true
                    }
                }
                false
            }
        }

        // Explore button listener
        val exploreButton = findViewById<Button>(R.id.exploreButton)
        exploreButton.setOnClickListener {
            val intent = Intent(this, IngredientActivity::class.java)
            startActivity(intent)
        }

        initializeSpeechRecognizer()

        // Set up microphone button for speech recognition
        val micButton = findViewById<ImageView>(R.id.micButton)
        micButton.setOnClickListener {
            if (isNetworkAvailable()) {
                if (isAudioPermissionGranted()) {
                    speechRecognizer.startListening(recognizerIntent)
                    showListeningFeedback(true)
                } else {
                    requestAudioPermission()
                }
            } else {
                Toast.makeText(this@HomeActivity, "No network connection. Please check your internet.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeSpeechRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

                if (SpeechRecognizer.isRecognitionAvailable(this)) {
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }

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
                        if (!matches.isNullOrEmpty()) {
                            val spokenText = matches[0]
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showListeningFeedback(isListening: Boolean) {
        // Show some visual feedback (e.g., change button state, show animation)
    }

    private fun isAudioPermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
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
            recyclerView?.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@HomeActivity, 1)
                randomRecipeAdapter = RandomRecipeAdapter(this@HomeActivity, response.recipes ?: emptyList())
                adapter = randomRecipeAdapter
            }
        }

        override fun didError(message: String) {
            dialog?.dismiss()
            Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}
