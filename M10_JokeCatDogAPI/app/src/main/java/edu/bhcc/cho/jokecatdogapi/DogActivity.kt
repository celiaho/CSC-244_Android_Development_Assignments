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

class DogActivity : AppCompatActivity() {
    private lateinit var dogNetworkImageView: NetworkImageView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState:Bundle ?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Show status bar
        setContentView(R.layout.activity_dog)
        // ??? Prefab Empty View code - No appreciable display difference in portrait but makes buttons run into app bar in landscape & pushes joke textview lower in portrait
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Set initial visibility based on current orientation
        handleStatusBarVisibility(resources.configuration.orientation)

        // Initialize views
        dogNetworkImageView = findViewById(R.id.niv_dog)
        backButton = findViewById(R.id.btn_back)

        // API request
        val url = "https://dog.ceo/api/breeds/image/random"
        val queue = Volley.newRequestQueue(this)
        val imageLoader = ImageLoader(queue, object : ImageLoader.ImageCache {
            private val mCache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(10)
            override fun putBitmap(url: String, bitmap: Bitmap) {
                mCache.put(url, bitmap)
//                catNetworkImageView.setImageBitmap(bitmap)
            }

            override fun getBitmap(url: String): Bitmap? {
                return mCache.get(url)
            }
        })

        // Load image
        dogNetworkImageView.setImageUrl(url, imageLoader)

        // Handle back button click
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("LAST_SELECTION_MESSAGE", "You last saw a dog.")
            startActivity(intent)
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