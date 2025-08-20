package edu.bhcc.cho.jokecatdogapi

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.LruCache
import android.view.View
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton

class PokemonActivity : AppCompatActivity() {
    private lateinit var pokemonNetworkImageView: NetworkImageView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState:Bundle ?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pokemon)
        // ??? Prefab Empty View code - No appreciable display difference in portrait but makes buttons run into app bar in landscape & pushes joke textview lower in portrait
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cat)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Set initial visibility based on current orientation
        handleStatusBarVisibility(resources.configuration.orientation)

        // Initialize views
        catNetworkImageView = findViewById(R.id.niv_cat)
        backButton = findViewById(R.id.btn_back)

        // Set up image loader with cache
        val url = "https://cataas.com/cat?type=square"
        val queue = Volley.newRequestQueue(this)
        val imageLoader = ImageLoader(queue, object : ImageLoader.ImageCache {
            private val mCache = LruCache<String, Bitmap>(10)
            override fun getBitmap(url: String): Bitmap? = mCache.get(url)
            override fun putBitmap(url: String, bitmap: Bitmap) {
                mCache.put(url, bitmap)
            }
        })

        // Set image
        catNetworkImageView.setImageUrl(url, imageLoader)

        // Handle back button click
        backButton.setOnClickListener {
            val lastSelectionMessage = "You last saw a cat."

            // Save last selection to disk
            val prefs = getSharedPreferences("JokeCatDogPrefs", MODE_PRIVATE)
            prefs.edit().putString("lastSelection", lastSelectionMessage).apply()

            // Pass last selection back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("LAST_SELECTION_MESSAGE", lastSelectionMessage)
            startActivity(intent)
        }

        // Adjust top guideline to be 18% down after ActionBar
        val root = findViewById<View>(R.id.cat)
        val guideline = findViewById<View>(R.id.guideline_top)

        root.viewTreeObserver.addOnGlobalLayoutListener {
            val screenHeight = root.height
            val actionBarHeight = supportActionBar?.height ?: 0

            val offset = ((screenHeight - actionBarHeight) * 0.18f + actionBarHeight).toInt()
            val params = guideline.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            params.guideBegin = offset
            guideline.layoutParams = params
        }
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