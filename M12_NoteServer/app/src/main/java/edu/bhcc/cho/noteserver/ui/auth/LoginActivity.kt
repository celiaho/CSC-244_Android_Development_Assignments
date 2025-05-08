package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.LoginRequest
import edu.bhcc.cho.noteserver.data.network.AuthApiService

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button
    private lateinit var forgotPasswordLink: TextView
    private lateinit var errorTextView: TextView

    private lateinit var authApiService: AuthApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        createAccountButton = findViewById(R.id.create_account_button)
        forgotPasswordLink = findViewById(R.id.forgot_password_link)
        errorTextView = findViewById(R.id.login_error)

        // Initialize API service
        authApiService = AuthApiService(this)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                errorTextView.text = "Please enter both email and password."
                errorTextView.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)

            authApiService.loginUser(
                request = loginRequest,
                onSuccess = {
                    errorTextView.visibility = View.GONE
                    // Redirect to Document Management screen
                    startActivity(Intent(this, edu.bhcc.cho.noteserver.ui.document.DocumentManagementActivity::class.java))
                    finish()
                },
                onError = {
                    errorTextView.text = it
                    errorTextView.visibility = View.VISIBLE
                }
            )
        }

        // Handle "Forgot Password?" link
        forgotPasswordLink.setOnClickListener {
            startActivity(Intent(this, PasswordForgotActivity::class.java))
        }

        // Handle "Create Account" button
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}