package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val continueButton = findViewById<Button>(R.id.signup_button)
        continueButton.setOnClickListener {
            startActivity(Intent(this, SignupVerificationActivity::class.java))
        }
    }

}

//package edu.bhcc.cho.noteserver.ui.auth
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import edu.bhcc.cho.noteserver.R
//import edu.bhcc.cho.noteserver.data.model.SignupRequest
//import edu.bhcc.cho.noteserver.data.network.AuthApiService
//
//class SignupActivity : AppCompatActivity() {
//    private lateinit var emailEditText: EditText
//    private lateinit var firstNameEditText: EditText
//    private lateinit var lastNameEditText: EditText
//    private lateinit var passwordEditText: EditText
//    private lateinit var createAccountButton: Button
//    private lateinit var errorTextView: TextView
//
//    private lateinit var authApiService: AuthApiService
//
//    private fun buildExtraInfo(): String {
//        val devicePlatform = "Android"
//        val appVersionName = packageManager.getPackageInfo(packageName, 0).versionName ?: "unknown" // from build.gradle
//        val sdkInt = android.os.Build.VERSION.SDK_INT
//        val deviceManufacturer = android.os.Build.MANUFACTURER ?: "unknown"
//        val deviceModel = android.os.Build.MODEL ?: "unknown"
//        val deviceLocale = resources.configuration.locales[0].toString() // e.g. "en_US"
//
//        // Return a string with user's device platform, app version name, SDK version, manufacturer/model, and locale
//        return "platform=$devicePlatform" +
//                "&appVersion=$appVersionName" +
//                "&sdk=$sdkInt" +
//                "&device=$deviceManufacturer $deviceModel" +
//                "&locale=$deviceLocale"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup)
//
//        emailEditText = findViewById(R.id.signup_email)
//        firstNameEditText = findViewById(R.id.signup_first_name)
//        lastNameEditText = findViewById(R.id.signup_last_name)
//        passwordEditText = findViewById(R.id.signup_password)
//        createAccountButton = findViewById(R.id.signup_button)
//        errorTextView = findViewById(R.id.signup_error)
//
//        authApiService = AuthApiService(this)
//
//        createAccountButton.setOnClickListener {
//            val email = emailEditText.text.toString()
//            val firstName = firstNameEditText.text.toString()
//            val lastName = lastNameEditText.text.toString()
//            val password = passwordEditText.text.toString()
//
//            if (email.isBlank() || firstName.isBlank() || lastName.isBlank() || password.isBlank()) {
//                errorTextView.text = "Please fill out all fields."
//                errorTextView.visibility = View.VISIBLE
//                return@setOnClickListener
//            }
//
//            // Wrap inputs in SignupRequest
//            val signupRequest = SignupRequest(
//                email = email,
//                password = password,
//                firstName = firstName,
//                lastName = lastName,
//                extra = buildExtraInfo() // String with device platform, app version name, SDK version, manufacturer/model, and locale
//            )
//
////            apiService.auth(request,
////                onSuccess = {
////                    Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
////                    // Go to login or main activity
////                },
////                onError = { error ->
////                    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
////                }
////            )
//
//            // Call signupUser function
//            authApiService.signupUser(
//                signupRequest,
//                onSuccess = {
//                    // Success logic
//                    errorTextView.visibility = View.GONE  // Hide error message if showing
//                    Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, LoginActivity::class.java)) // Go to login activity
//                    finish()
//                },
//                onError = { error ->
//                    // Handle error
//                    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
//                    errorTextView.text = error
//                    errorTextView.visibility = View.VISIBLE
//                }
//            )
//        }
//    }
//}