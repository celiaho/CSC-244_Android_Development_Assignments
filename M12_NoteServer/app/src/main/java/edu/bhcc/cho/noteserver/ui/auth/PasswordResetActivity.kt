package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class PasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        val resetBtn = findViewById<Button>(R.id.reset_continue_button)
        resetBtn.setOnClickListener {
            Toast.makeText(this, "Password changed. Log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}