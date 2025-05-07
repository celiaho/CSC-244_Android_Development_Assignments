package edu.bhcc.cho.noteserver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.ui.document.DocumentManagementActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button
    private lateinit var forgotPasswordLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        signInButton = findViewById(R.id.btnLogin)
        forgotPasswordLink = findViewById(R.id.tvForgotPassword)

        signInButton.setOnClickListener {
            Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DocumentManagementActivity::class.java))
        }

        forgotPasswordLink.setOnClickListener {
            startActivity(Intent(this, PasswordForgotActivity::class.java))
        }

        passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signInButton.performClick()
                true
            } else false
        }
    }
}