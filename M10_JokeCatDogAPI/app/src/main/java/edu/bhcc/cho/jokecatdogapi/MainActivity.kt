package edu.bhcc.cho.jokecatdogapi

import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        val toolbar = findViewById<MaterialToolbar>(R.id.appBar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.title = getString(R.string.app_name)
    }
}