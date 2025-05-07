package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.ui.document.DocumentActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var forgotPasswordLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Delay hiding until layout is fully loaded
        window.decorView.post {
            var iconRow = findViewById<View>(R.id.icon_row)
            iconRow?.visibility = View.GONE

            val appNameTextView = findViewById<TextView>(R.id.app_name)
            appNameTextView.text = "GOTT NOTES"
            appNameTextView.maxLines = 1
        }

        // Init form views
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        errorTextView = findViewById(R.id.login_error)
        forgotPasswordLink = findViewById(R.id.forgot_password_link)

        // Forgot password redirect
        forgotPasswordLink.setOnClickListener {
            startActivity(Intent(this, PasswordForgotActivity::class.java))
        }

        // Handle login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                errorTextView.text = "Please enter both email and password."
                errorTextView.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // TODO: Replace with actual server login
            if (email == "test@example.com" && password == "password") {
                errorTextView.visibility = View.GONE
                startActivity(Intent(this, DocumentActivity::class.java))
                finish()
            } else {
                errorTextView.text = "Invalid credentials. Please try again."
                errorTextView.visibility = View.VISIBLE
            }
        }
    }
}