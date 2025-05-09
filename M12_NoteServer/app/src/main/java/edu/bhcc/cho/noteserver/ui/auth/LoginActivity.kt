package edu.bhcc.cho.noteserver.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.LoginRequest
import edu.bhcc.cho.noteserver.data.network.AuthApiService
import edu.bhcc.cho.noteserver.ui.document.DocumentManagementActivity
import edu.bhcc.cho.noteserver.utils.SessionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button
    private lateinit var forgotPasswordLink: TextView
    private lateinit var errorTextView: TextView

    private lateinit var apiService: AuthApiService
    private lateinit var sessionManager: SessionManager

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

        apiService = AuthApiService(this)
        sessionManager = SessionManager(this)

        // Attempt to automatically show the keyboard on email field
        emailEditText.postDelayed({
            emailEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT)
//            emailEditText.setOnFocusChangeListener { _, hasFocus ->
//                if (hasFocus) {
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT)
//                }
//            }
        }, 100)  // Delay to ensure the layout is fully loaded

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                errorTextView.text = "Please enter both email and password."
                errorTextView.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Send login request to API via AuthApiService
            val loginRequest = LoginRequest(email, password)
            // Call /auth/login and save token
            apiService.loginUser(
                request = loginRequest,
                onSuccess = { token ->
                    sessionManager.saveToken(token)

                    // Call /profiles/me to fetch and save user ID
                    apiService.getMyProfile(
                        onSuccess = { profile ->
                            val userId = profile.optString("id", "")
                            sessionManager.saveUserId(userId)
                            errorTextView.visibility = View.GONE
                            startActivity(Intent(this, DocumentManagementActivity::class.java))
                            finish()
                        },
                        onError = {
                            errorTextView.text = "Login succeeded but failed to fetch user profile."
                            errorTextView.visibility = View.VISIBLE
                        }
                    )
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