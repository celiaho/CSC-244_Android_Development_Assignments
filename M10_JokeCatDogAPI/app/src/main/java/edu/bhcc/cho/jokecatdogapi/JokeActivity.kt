package edu.bhcc.cho.jokecatdogapi

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class JokeActivity : AppCompatActivity() {
    private lateinit var jokeTextView: TextView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState:Bundle ?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_joke)
        // ??? No appreciable display difference (Prefab Android Studio code)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cat)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Initialize views
        jokeTextView = findViewById(R.id.tv_joke)
        backButton = findViewById(R.id.btn_back)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }
    }
}