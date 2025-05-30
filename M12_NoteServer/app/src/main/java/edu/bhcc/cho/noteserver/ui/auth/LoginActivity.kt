package edu.bhcc.cho.noteserver.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.LoginRequest
import edu.bhcc.cho.noteserver.data.network.AuthApiService
import edu.bhcc.cho.noteserver.ui.document.DocumentActivity
import edu.bhcc.cho.noteserver.ui.document.DocumentManagementActivity
import edu.bhcc.cho.noteserver.utils.JwtUtils
import edu.bhcc.cho.noteserver.utils.SessionManager
import org.json.JSONObject
import java.time.Instant
import kotlin.toString

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

        Log.d("---LOGIN_PAGE_LOADED", "---LOGIN_PAGE_LOADED")

//        // If valid token exists, skip login and go to DocumentManagementActivity - REPLACED BY SPLASH LOGIC
//        val token = sessionManager.getToken()
//        if (!token.isNullOrBlank() && !JwtUtils.isTokenExpired(token)) {
//            startActivity(Intent(this, DocumentManagementActivity::class.java))
//            finish()
//            return  // Don't run login screen logic
//        }

        // Log token and User ID
        val prefs = getSharedPreferences("GottNotesSession", MODE_PRIVATE)
        for ((key, value) in prefs.all) {
            Log.d("SharedPrefs", "$key = ${value.toString()}")
        }

//        // Attempt to automatically show the keyboard on email field
//        emailEditText.postDelayed({
//            emailEditText.requestFocus()
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT)
////            emailEditText.setOnFocusChangeListener { _, hasFocus ->
////                if (hasFocus) {
////                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////                    imm.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT)
////                }
////            }
//        }, 100)  // Delay to ensure the layout is fully loaded

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
            // Call /auth/login and save token, user ID, and expiration time
            apiService.loginUser(
                request = loginRequest,
                onSuccess = { token ->
                    val currentTime = System.currentTimeMillis() / 1000
                    val tokenExpirationTime = JwtUtils.getExpirationTime(token.toString())
                    val tokenIssuedAtTime = JwtUtils.getIssuedAtTime(token.toString())

                    // Log new token issue time, new token expiration time, and current system time
                    Log.d("---NEW_TOKEN_ISSUED", "---iat = $tokenIssuedAtTime (unix seconds)")
                    Log.d("---NEW_TOKEN_EXPIRATION", "---exp = $tokenExpirationTime (unix seconds)") // JwtUtils.getExpirationTime(token)?.toString() ?: "null"
                    Log.d("---SYSTEM_TIME", "---now = $currentTime (unix seconds)")
                    // Log above in readable format
                    Log.d("---NEW_TOKEN_ISSUED", "---iat (readable) = ${Instant.ofEpochSecond(tokenIssuedAtTime ?: 0)}")
                    Log.d("---NEW_TOKEN_EXPIRATION", "---exp (readable) = ${Instant.ofEpochSecond(tokenExpirationTime ?: 0)}")
                    Log.d("---SYSTEM_TIME", "---now (readable) = ${Instant.ofEpochSecond(currentTime)}")

                    //// Test code (all lines marked with/below //// in this block)
                    Handler(Looper.getMainLooper()).postDelayed({ //// Test code: Band-aid patch to delay profile fetch to see if server clock settles and allows login
//                    //// Workaround block to replace getMyProfile() and server rejecting fresh tokens
                        if (tokenExpirationTime != null) {
                            // Extract userId from token
                            val userId = JwtUtils.getUserId(token)

                            if (userId != null) {
                                apiService.getProfileById(
                                    userId = userId,
                                    onSuccess = { profile ->
                                        sessionManager.saveSession(token, userId, (tokenExpirationTime + 3600) * 1000)
                                        errorTextView.visibility = View.GONE
                                        startActivity(Intent(this, DocumentActivity::class.java))
                                        finish()
                                    },
                                    onError = {
                                        errorTextView.text = "Login succeeded but failed to fetch user profile (by ID)."
                                        errorTextView.visibility = View.VISIBLE
                                    }
                                )
                            } else {
                                errorTextView.text = "Login succeeded but token has no expiration."
                                errorTextView.visibility = View.VISIBLE
                            }
                        //// This block replaced with workaround test code above
//                        apiService.getMyProfile(
//                        onSuccess = { profile ->
//                            val userId = profile.optString("id", "")
//                            if (tokenExpirationTime != null) {
//                                // sessionManager.saveSession(token, userId, tokenExpirationTime * 1000) // original code
//                                //// TEMP PATCH: Add 1 hour (3600 seconds) to expiration to compensate for server clock skew
//                                // ⚠️ TEMP PATCH: Server rejects fresh JWTs due to clock skew
//                                // Adding +3600s buffer to token expiration time to bypass 401 "expired" error
//                                // REMOVE THIS BEFORE FINAL SUBMISSION IF SERVER IS FIXED
//                                val patchedExpirationMillis = (tokenExpirationTime + 3600) * 1000
//                                sessionManager.saveSession(token, userId, patchedExpirationMillis)
//                            }
//                            errorTextView.visibility = View.GONE
//                            startActivity(Intent(this, DocumentActivity::class.java))
//                            finish()
//                        },
//                        onError = {
//                            errorTextView.text = "Login succeeded but failed to fetch user profile."
//                            errorTextView.visibility = View.VISIBLE
//                        }
//                    )
                    }, 1000) //// Band-aid test code: delay 1 second (1000ms)
//                },
//                onError = {
//                    errorTextView.text = it
//                    errorTextView.visibility = View.VISIBLE
//                }
//            )
        }

        // Handle "Forgot Password?" link
        forgotPasswordLink.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            //// Pass Login email to PasswordForgot screen - DOESN'T WORK
            val intent = Intent(this, PasswordForgotActivity::class.java)
            intent.putExtra("EMAIL", email)
            startActivity(intent)
        }

        // Handle "Create Account" button
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}