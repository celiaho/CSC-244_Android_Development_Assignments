package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class PasswordForgotActivity : AppCompatActivity() {

    // Initialize layout elements
    private lateinit var backLink: TextView
    private lateinit var emailInput: EditText
    private lateinit var errorText: TextView
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_forgot)

        // Get references to views
        backLink = findViewById(R.id.forgot_back)
        emailInput = findViewById(R.id.forgot_email)
        errorText = findViewById(R.id.forgot_error)
        continueButton = findViewById(R.id.forgot_continue_button)

        // Handle continue button click
        continueButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            Log.d("ForgotPassword", "Email entered: $email") // Check if email is being passed correctly

            if (email.isEmpty()) {
                errorText.text = "Please enter your email address."
                errorText.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            // For demo purposes, we skip verification
            errorText.visibility = TextView.GONE

            // Hide keyboard
            currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            // Go to reset screen
            val intent = Intent(this, PasswordResetActivity::class.java)
            intent.putExtra("EMAIL", email)
            startActivity(intent)
            Log.d("ForgotPassword", "Navigating to PasswordResetActivity") // Log this to ensure navigation
        }

        // Back link returns to Login screen
        backLink.setOnClickListener {
            finish() // Or "startActivity(Intent(this, LoginActivity::class.java))"
        }
    }
}