package edu.bhcc.cho.jokecatdogapi

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        // ??? No appreciable display difference (Prefab Android Studio code)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cat)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Initialize views
        lastSelectionTextView = findViewById(R.id.tv_last_selection)
        jokeButton = findViewById(R.id.btn_joke)
        catButton = findViewById(R.id.btn_cat)
        dogButton = findViewById(R.id.btn_dog)

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

    }
}