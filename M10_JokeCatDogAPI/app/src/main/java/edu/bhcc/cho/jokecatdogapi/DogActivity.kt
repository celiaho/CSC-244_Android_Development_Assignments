package edu.bhcc.cho.jokecatdogapi

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class DogActivity : AppCompatActivity() {
    private lateinit var dogImageView: ImageView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState:Bundle ?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Show status bar
        setContentView(R.layout.activity_dog)
        // Prefab Android Studio code - No appreciable display difference
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dog)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Initialize views
        dogImageView = findViewById(R.id.iv_dog)
        backButton = findViewById(R.id.btn_back)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }
    }
}