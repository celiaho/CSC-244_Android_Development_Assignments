package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class PasswordForgotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_forgot)

        val btnContinue = findViewById<Button>(R.id.forgot_continue_button)
        btnContinue.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }
    }

}