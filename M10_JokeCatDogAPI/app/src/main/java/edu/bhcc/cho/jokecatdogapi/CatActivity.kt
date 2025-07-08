package edu.bhcc.cho.jokecatdogapi

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.View
import android.view.WindowInsets
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton

class CatActivity : AppCompatActivity() {
    private lateinit var catNetworkImageView: NetworkImageView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState:Bundle ?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cat)
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

        // API request
        val url = "https://cataas.com/cat"
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
        catNetworkImageView.setImageUrl(url, imageLoader)

        // Handle back button click
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("LAST_SELECTION_MESSAGE", "You last saw a cat.")
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