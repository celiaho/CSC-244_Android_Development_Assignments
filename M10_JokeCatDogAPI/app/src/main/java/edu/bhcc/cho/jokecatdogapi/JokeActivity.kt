package edu.bhcc.cho.jokecatdogapi

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton

class JokeActivity : AppCompatActivity() {
    private lateinit var jokeTextView: TextView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_joke)
        // ??? Prefab Empty View code - No appreciable display difference in portrait but makes buttons run into app bar in landscape & pushes joke textview lower in portrait
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cat)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Set initial visibility based on current orientation
        handleStatusBarVisibility(resources.configuration.orientation)

        // Initialize views
        jokeTextView = findViewById(R.id.tv_joke)
        backButton = findViewById(R.id.btn_back)

        // API request
        val url = "https://v2.jokeapi.dev/joke/Any?type=single"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener { response ->
                jokeTextView.text = response.get("joke").toString()
                Log.d("JokeActivity", "Joke: ${response.get("joke")}")
            },
            Response.ErrorListener { error ->
                jokeTextView.text = "Error: ${error.message}"
                Log.d("JokeActivity", "Error: ${error.message}")
            }
        )
        queue.add(jsonObjectRequest)

        // Handle back button click
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("LAST_SELECTION_MESSAGE", "You last saw a joke.")
            startActivity(intent)
        }

//        private fun switchActivities() {
//            val switchActivityIntent: Intent = Intent(
//                this,
//                MainActivity::class.java
//            )
//            switchActivityIntent.putExtra("page", "last visited dog image")
//            startActivity(switchActivityIntent)
//        }
    }

// Handle orientation changes
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    handleStatusBarVisibility(newConfig.orientation)
}

// Helper method to handle status bar visibility based on orientation
private fun handleStatusBarVisibility(orientation: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // API 30+ modern method
        val controller = window.insetsController
        if (controller != null) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                controller.hide(WindowInsets.Type.statusBars())
            } else {
                controller.show(WindowInsets.Type.statusBars())
            }
        }
    } else {
        // API 26–29 legacy method
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}
}