package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class SignupVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_verification)

        val completeBtn = findViewById<Button>(R.id.verify_button)
        completeBtn.setOnClickListener {
            Toast.makeText(this, "Account created. Log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}