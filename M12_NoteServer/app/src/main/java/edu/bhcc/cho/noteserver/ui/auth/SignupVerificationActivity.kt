package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class SignupVerificationActivity : AppCompatActivity() {
    private lateinit var backRedirect: TextView
    private lateinit var tempPasswordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var completeBtn: Button

    // Simulated OTP for demo purposes (normally checked against the server)
    private val correctTempPassword = "tempPassword123" // Replace with actual logic when deploying app

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_verification)

        // Initialize views
        backRedirect = findViewById(R.id.verify_back)
        tempPasswordEditText = findViewById(R.id.verify_temp_password)
        errorTextView = findViewById(R.id.verify_error)
        completeBtn = findViewById(R.id.verify_button)

        // "< Back" link to LoginActivity
        backRedirect.setOnClickListener { finish() }  // return to Login

        // Temp password disclosure
        Toast.makeText(this, "The temporary password sent to your email is: tempPassword123", Toast.LENGTH_LONG).show()

        // "Complete Signup" button click
        completeBtn.setOnClickListener {
            val enteredTempPassword = tempPasswordEditText.text.toString().trim()

            // Validation for no input
            if (enteredTempPassword.isEmpty()) {
                errorTextView.text = "Please enter the temporary password."
                errorTextView.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Check if the entered password is correct
            if (enteredTempPassword == correctTempPassword) {
                // Show success message and navigate to LoginActivity
                Toast.makeText(this, "Account created. Please log in.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))  // Navigate to login page
                finish()  // Close current activity
            } else {
                // Show error if the temporary password is incorrect
                errorTextView.text = "Invalid temporary password. Please try again."
                errorTextView.visibility = View.VISIBLE
            }
        }
    }
}