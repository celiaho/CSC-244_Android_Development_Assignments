## Project Summary

I'm building an Android note-taking app in Kotlin using Android Studio. It connects to a Swagger-documented REST API called DocServer via Gson (not Moshi) and Volley. The backend server is NoteServer.jar. The app handles user authentication, document storage, sharing, and querying.

The app uses only XML layouts (no Jetpack Navigation/Fragments). Each screen has its own activity and custom toolbar. The app theme supports light/dark mode. All authentication logic uses JWTs with expiration, stored via SharedPreferences. There is no refresh token — the app logs out when the token is expired.

All project files follow consistent Material Design spacing, touch target sizing, input style conventions, and accessibility guidelines for color contrast. The project is a graded assignment with strict rubric-based requirements.


## ✅ Current Fix List (as of 2025-05-20)

Keep this list updated going forward:

| Priority | Feature or Bug Description                                   | Status       | Notes                                                             |
|----------|--------------------------------------------------------------|--------------|-------------------------------------------------------------------|
| 🔴 High  | Implement splash screen with auto-login routing              | ❌ Not Fixed | Prevents expired token issues from kicking users mid-session     |
| 🔴 High  | Add local cache + autosave to DocumentActivity               | ❌ Not Fixed | Required by professor’s rubric                                   |
| 🔴 High  | Fix broken Document Share popup                              | ❌ Not Fixed | Currently shows toast only, popup doesn't display                |
| 🔴 High  | Fix "My Files" and "Shared With Me" tab displays             | ❌ Not Fixed | API data not populating or displaying                            |
| 🟠 Med   | Make Settings toolbar icons functional                       | ❌ Not Fixed | Icons visible but not wired up                                   |
| 🟡 Low   | Add padding to input fields                                  | ✅ Fixed     | Improves input readability                                       |
| ⚪️ Very Low | Auto-show keyboard on email/password fields                | 💤 Deferred  | Cosmetic; least priority                                         |



## 📋 Full Project Requirements (Paraphrased by ChatGPT)

The objective of this assignment is to create a note-taking app. The app must save all data to the remote server provided. The server code is written for you (DocServer). Full API documentation is available.

Your application must be able to do the following:

1) Documents created on the phone should automatically save to the server. While the user works on a document, the document should automatically save to the local HD or saved instance state (local cache). Using a background task, the document should be routinely saved to the server to prevent data loss. Remote saving should especially happen before activity death! When the app is minimized, load from cache first before requesting from server. Cache is cleared when a new user logs in.

2) The App retrieves documents saved on the server and loads them on the phone when the user wants. The user should have a screen that enables them to load a document they have saved previously to make edits.

3) The user should have the ability to create an account, change their password, edit their account information, and sign out. To view and modify documents, the user needs a valid JWT token. You must:
   - Store token locally before app destruction
   - Automatically sign in with valid token
   - Handle expired tokens by prompting for login

4) The user will be able to share their documents with other users who have accounts on the server. Sharing settings are editable in a popup window. File management screen should show “My Files” and “Shared With Me” documents. Changes to document sharing should reflect via refresh (manual or auto).

GUI must follow provided prototype. All features must be demonstrated in the submission video. You must use Volley or a similar library to connect to NoteServer.jar. No 3rd-party backends allowed.


## Professor's Project Requirements (Verbatim)
The objective of this assignment is to create a note-taking app. The app must save all data to the remote server provided. Don't worry, the server code is written for you (See DocServer https://github.com/HWilliams64/docserver). In the link below, you will find full API documentation for the server.

Your application must be able to do the following:

1) Documents created on the phone should automatically save to the server. While the user works on a document, the document should automatically save to the local HD or saved instance state; let's call this the local cache. Using a background task document should be routinely saved to the server to prevent data loss. Remote saving of the document should especially happen before activity death! In the event the app is minimized, to save time, the app should retrieve the document from the local cache before attempting to retrieve it from the server version. Keep in mind if a new user signs in the local cache is cleared. 

2) The App retrieves documents saved on the server and loads them on the phone when the user wants. The user should have a screen that enables them to load a document they have saved previously to make edits to it.

3) The user should have the ability to create an account, change their password, edit their account information, and sign out of their account. Keep in mind that to view and modify documents, the user will need an authorization token from the server prior to making those types of HTTP requests. They will receive an authorization token when they sign in or when they refresh an existing authorization token. Please make sure your application manages the authorization token wisely. In other words, make sure you mind the expiration date of the token, and you refresh your token a few seconds before the expiration date. Furthermore, to save the user time, set up an automatic user sign-in logic. Before the app is destroyed, save the token locally so that it may be retrieved the next time the App is opened. If the token has expired or does not exist when the app is opened the user is taken to the sign-in page. 

3) The user will be able to share their documents with other users. The user should be able to share their documents with whoever has an account on the server. The user should be able to easily modify their sharing settings from a particular document from a popup window in the app. In a separate activity (file management), the user should be able to see all the documents shared with them and all the documents they have created. One thing you should note is that if another user shares or hides a document with our user when they are in the file management activity, there should be a manual or auto-refresh capability to reflect the change.  

For guidelines as to what your app's GUI should look like please reference the link labeled "Prototype" below. For an understanding as to how to start the server and make requests please take a look at the link "Server Documentation" below. The server's executable is the attached jar file labeled "NoteServer.jar".  

Please make sure your submission video(s) accurately display all aspects of the app outlined in the list above. Failure to demonstrate a feature will lead to a zero for that particular aspect.  

The app must save all data to the remote server provided, you are not allowed to use other 3rd party Databases or backends. for this assignment. Please remember that you must use Volley or a similar library to connect with the NoteServer.jar server to manage your note documents and access permissions in your app. 

This Grader Than Workspace can serve as dev environment for classroom demonstrations https://portal.graderthan.com/school/enroll/crs-a13W01Q/

Prototype https://docs.google.com/presentation/d/1vhXbZUPC9heZNvBlZwh7_QUijjSc5iEDXh28HgriC60/edit?usp=sharing

Server Docs and Download

Take a look at the following videos for an understanding of complete submission:

Create a user 1 https://bhcc.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=35b8c324-58fe-4d93-9fba-ad6d0131ed79

User 1 password reset https://bhcc.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=8f4e5dec-8c7a-4496-8200-ad6d0131ed79

Create a user 2 https://bhcc.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=00a1b1cf-41d3-4ed3-8ef3-ad6d0131ed79

Create and share a document https://bhcc.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=31d18c1c-3899-4945-9fac-ad6d0131ed79

Display shared documents from User 2 https://bhcc.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=64caa9b1-4507-4a7e-8fbc-ad6d0131ed79


## 🛠️ Project Summary and Tech Stack

- App Name: **GOTT NOTES**
- Platform: Android native
- Language: Kotlin
- Networking: **Volley** (No Retrofit allowed)
- Backend: **DocServer.jar** (Swagger-documented, educational JWT-based API)
- Data Format: JSON
- Screens/Activities:
  - Login
  - Signup
  - Forgot Password
  - Document Editor (autosaves, caches locally and remotely)
  - Document Management (tabs: My Files / Shared With Me)
  - Settings (theme toggle, name/pass edit)
  - Document Share Popup (ListView of users)
- Design: Based on professor’s prototype
- Layouts: XML (no Jetpack Compose or Navigation Graph)
- Token storage: `SessionManager` using SharedPreferences
- JWT parsing: `JwtUtils.kt`
- SharedPreferences filename: `GottNotesSession`
- Tab switching: Custom implementation
- Visual style: Light & Dark themes supported


## Current Stack

- Kotlin with Android XML layouts
- Volley for networking
- Gson for serialization
- SharedPreferences for session state
- JWTs (no refresh tokens; decoded locally)
- SessionManager.kt handles auth token/user ID/expiration
- JwtUtils.kt extracts exp from token
- Activities:
  - LoginActivity
  - SignupActivity
  - PasswordForgotActivity
  - PasswordResetActivity
  - DocumentManagementActivity (with tabbed list)
  - DocumentActivity (editor, auto-save planned)
  - DocumentSharePopupActivity (ListView, user selection)
  - SettingsActivity
- Document data comes from the DocServer API (no local DB; only JSON caching)


## Current LoginActivity.kt (Ask me for other current code as needed)

package edu.bhcc.cho.noteserver.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

        // If token exists, skip login and go to DocumentManagementActivity
        val token = sessionManager.getAuthToken()
        if (!token.isNullOrBlank() && !JwtUtils.isTokenExpired(token)) {
            startActivity(Intent(this, DocumentManagementActivity::class.java))
            finish()
            return  // Don't run login screen logic
        }

        // Log token and User ID
        val prefs = getSharedPreferences("GOTTNotesSession", MODE_PRIVATE)
        for ((key, value) in prefs.all) {
            Log.d("SharedPrefs", "$key = $value")
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
                    val tokenExpirationTime = JwtUtils.getExpirationTime(token)

                    apiService.getMyProfile(
                        onSuccess = { profile ->
                            val userId = profile.optString("id", "")
                            if (tokenExpirationTime != null) {
                                sessionManager.saveSession(token, userId, tokenExpirationTime * 1000)
                            }
                            errorTextView.visibility = View.GONE
                            startActivity(Intent(this, DocumentActivity::class.java))
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

## Immediate Task

Next step: Implement a `LauncherActivity` (splash screen with app name) that checks for token validity and routes to either `LoginActivity` or `DocumentManagementActivity`.