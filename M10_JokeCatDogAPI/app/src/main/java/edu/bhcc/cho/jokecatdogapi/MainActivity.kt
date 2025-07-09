package edu.bhcc.cho.jokecatdogapi

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var lastSelectionTextView: TextView
    private lateinit var jokeButton: MaterialButton
    private lateinit var catButton: MaterialButton
    private lateinit var dogButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // ??? Prefab Empty View code - No appreciable display difference in portrait but makes buttons run into app bar in landscape & pushes joke textview lower in portrait
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Set initial visibility based on current orientation
        handleStatusBarVisibility(resources.configuration.orientation)

        // Initialize views
        lastSelectionTextView = findViewById(R.id.tv_last_selection)
        jokeButton = findViewById(R.id.btn_joke)
        catButton = findViewById(R.id.btn_cat)
        dogButton = findViewById(R.id.btn_dog)

        // Set Flow orientation based on current screen orientation
        val flow = findViewById<Flow>(R.id.btn_flow)
        val orientation = resources.configuration.orientation
        flow.setOrientation(
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                Flow.VERTICAL
            else
                Flow.HORIZONTAL
        )

        // Retrieve and display last selection message from Shared Preferences
        val prefs = getSharedPreferences("JokeCatDogPrefs", MODE_PRIVATE)
        val lastSelectionMessage = prefs.getString("lastSelection", "")
        lastSelectionTextView.text = lastSelectionMessage

        // *HANDLE BUTTONS*
        // Handle joke button click
        jokeButton.setOnClickListener {
            startActivity(Intent(this, JokeActivity::class.java))
        }
        // Handle cat button click
        catButton.setOnClickListener {
            startActivity(Intent(this, CatActivity::class.java))
        }
        // Handle dog button click
        dogButton.setOnClickListener {
            startActivity(Intent(this, DogActivity::class.java))
        }

        // Adjust top guideline to be 18% down after ActionBar
        val root = findViewById<View>(R.id.main)
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

        // Update status bar visibility
        handleStatusBarVisibility(newConfig.orientation)

        // Update Flow button orientation on rotation
        val flow = findViewById<Flow>(R.id.btn_flow)
        flow.setOrientation(
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) Flow.VERTICAL
            else Flow.HORIZONTAL
        )
    }

    // Toggle status bar visibility based on orientation
    private fun handleStatusBarVisibility(orientation: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // API 30+ modern method
//            val controller = window.insetsController
//            if (window.insetsController != null) {
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    controller.hide(WindowInsets.Type.statusBars())
//                } else {
//                    controller.show(WindowInsets.Type.statusBars())
//                }
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // API 30+ modern method
                window.insetsController?.let { controller ->
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        controller.hide(WindowInsets.Type.statusBars())
                    } else {
                        controller.show(WindowInsets.Type.statusBars())
                    }
                }
            } else {
                // API 26–29 legacy method
                window.decorView.systemUiVisibility =
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                    } else {
                        View.SYSTEM_UI_FLAG_VISIBLE
                    }
            }
//
//        } else {
//            // API 26–29 legacy method
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            } else {
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//            }
//        }
    }
}