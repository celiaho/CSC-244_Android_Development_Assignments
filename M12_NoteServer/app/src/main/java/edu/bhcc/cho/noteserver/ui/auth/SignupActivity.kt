package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.SignupRequest
import edu.bhcc.cho.noteserver.data.network.AuthApiService

class SignupActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var loginRedirect: TextView

    private lateinit var authApiService: AuthApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        firstNameEditText = findViewById(R.id.signup_first_name)
        lastNameEditText = findViewById(R.id.signup_last_name)
        emailEditText = findViewById(R.id.signup_email)
        passwordEditText = findViewById(R.id.signup_password)
        signupButton = findViewById(R.id.signup_button)
        errorTextView = findViewById(R.id.signup_error)
        loginRedirect = findViewById(R.id.signup_login_link)

        // Initialize API service
        authApiService = AuthApiService(this)

        // Navigate back to LoginActivity
        loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Handle signup logic
        signupButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // Basic validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorTextView.text = "All fields are required."
                errorTextView.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (password.length < 8) {
                errorTextView.text = "Password must be at least 8 characters."
                errorTextView.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val request = SignupRequest(email, password, firstName, lastName, extra = null)

            // Call the API
            authApiService.signupUser(
                request,
                onSuccess = {
                    errorTextView.visibility = View.GONE
                    Toast.makeText(this, "Signup successful. Please log in.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onError = {
                    errorTextView.text = it
                    errorTextView.visibility = View.VISIBLE
                }
            )
        }
    }
}