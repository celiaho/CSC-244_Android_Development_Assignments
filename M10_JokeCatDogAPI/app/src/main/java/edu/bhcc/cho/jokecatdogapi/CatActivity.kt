package edu.bhcc.cho.jokecatdogapi

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class CatActivity : AppCompatActivity() {
    private lateinit var catImageView: ImageView
    private lateinit var backButton: MaterialButton

    override fun onCreate(savedInstanceState:Bundle ?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cat)
        // ??? No appreciable display difference (Prefab Android Studio code)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cat)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Initialize views
        catImageView = findViewById(R.id.iv_cat)
        backButton = findViewById(R.id.btn_back)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }
    }
}