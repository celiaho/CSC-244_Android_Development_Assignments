package edu.bhcc.cho.noteserver.ui.settings

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.Profile
import edu.bhcc.cho.noteserver.data.network.ProfileApiService
import edu.bhcc.cho.noteserver.utils.SessionManager
import android.view.View

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeToggle: SwitchCompat
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var saveButton: Button

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ProfileApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Highlight Settings icon in toolbar
        findViewById<ImageButton>(R.id.icon_settings)
            .setColorFilter(ContextCompat.getColor(this, R.color.orange))

        // Initialize shared preferences and API
        sessionManager = SessionManager(this)
        apiService = ProfileApiService(this)

        // Connect UI elements
        themeToggle = findViewById(R.id.theme_toggle)
        firstNameInput = findViewById(R.id.settings_first_name)
        lastNameInput = findViewById(R.id.settings_last_name)
        currentPasswordInput = findViewById(R.id.settings_current_password)
        newPasswordInput = findViewById(R.id.settings_new_password)
        saveButton = findViewById(R.id.settings_save_changes)

        Log.d("---SETTINGS_PAGE_LOADED", "---SETTINGS_PAGE_LOADED")

        // Load current theme toggle state
        val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        themeToggle.isChecked = isDark

        // Toggle listener for theme
        themeToggle.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            recreate() // restart activity to apply theme
        }

        // Save button logic
        saveButton.setOnClickListener {
            val currentPassword = currentPasswordInput.text.toString().trim()
            val newPassword = newPasswordInput.text.toString().trim()
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()

            if (currentPassword.isEmpty()) {
                showToast("Please enter your current password.")
                return@setOnClickListener
            }

            val extraFields = mutableMapOf<String, Any>("current_password" to currentPassword)
            if (newPassword.isNotBlank()) {
                extraFields["new_password"] = newPassword
            }

            val updatedProfile = Profile(
                id = "", // not needed for update
                email = "", // not needed for update
                firstName = firstName,
                lastName = lastName,
                creationDate = "",
                lastModifiedDate = "",
                extra = extraFields
            )

            apiService.updateProfile(
                updatedProfile,
                onSuccess = {
                    showToast("Changes saved.")
                },
                onError = { error ->
                    showToast("Error: ${error.message}")
                }
            )
        }

        // Fill in fields with current data
        loadProfile()
    }

    private fun loadProfile() {
        apiService.getProfile(
            onSuccess = { profile ->
                firstNameInput.setText(profile.firstName)
                lastNameInput.setText(profile.lastName)
            },
            onError = {
                showToast("Failed to load profile.")
            }
        )
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}