package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.network.AuthApiService

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var tempPasswordField: EditText
    private lateinit var newPasswordField: EditText
    private lateinit var errorText: TextView
    private lateinit var continueButton: Button
    private lateinit var backLink: TextView
    private lateinit var apiService: AuthApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        // Handle Back link click
        backLink.setOnClickListener { finish() }

        // Show a demo OTP for simulation purposes
        Toast.makeText(this, "The temporary password sent to your email is: tempPassword123", Toast.LENGTH_LONG).show()

        // Initialize views
        emailInput = findViewById(R.id.reset_email)
        tempPasswordField = findViewById(R.id.reset_temp_password)
        newPasswordField = findViewById(R.id.reset_new_password)
        errorText = findViewById(R.id.reset_error)
        continueButton = findViewById(R.id.reset_continue_button)
        backLink = findViewById(R.id.reset_back)
        apiService = AuthApiService(this)

        // Prefill email if passed from Forgot Password screen
        intent.getStringExtra("EMAIL")?.let { emailInput.setText(it) }

        // Handle the continue/reset button click
        continueButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val otp = tempPasswordField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()

            // Basic validation
            if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
                errorText.text = "All fields are required."
                errorText.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            if (newPassword.length < 8) {
                errorText.text = "New password must be at least 8 characters."
                errorText.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            // Hide keyboard after input
            currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            errorText.visibility = TextView.GONE

            // Call API to reset password using simulated OTP
            apiService.resetPassword(
                email = email,
                newPassword = newPassword,
                otp = otp,
                onSuccess = {
                    Toast.makeText(this, "Password changed. Please log in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onError = {
                    errorText.text = it
                    errorText.visibility = TextView.VISIBLE
                }
            )
        }
    }
}