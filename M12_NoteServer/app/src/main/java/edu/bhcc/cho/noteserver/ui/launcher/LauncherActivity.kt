package edu.bhcc.cho.noteserver.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.ui.auth.LoginActivity
import edu.bhcc.cho.noteserver.ui.document.DocumentManagementActivity
import edu.bhcc.cho.noteserver.utils.JwtUtils
import edu.bhcc.cho.noteserver.utils.SessionManager
import java.time.Instant

/**
 * LauncherActivity serves as a splash screen that routes to the appropriate activity
 * depending on whether a valid JWT token is found in SharedPreferences.
 */
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val sessionManager = SessionManager(this)
        val token = sessionManager.getToken()

        val currentTime = System.currentTimeMillis() / 1000
        val tokenExpirationTime = JwtUtils.getExpirationTime(token.toString())
        val tokenIssuedAtTime = JwtUtils.getIssuedAtTime(token.toString())

        Log.d("---SPLASH_PAGE_LOADED", "---SPLASH_PAGE_LOADED")

        // Check token validity and route accordingly
        val nextActivity = if (!token.isNullOrBlank() && !JwtUtils.isTokenExpired(token)) {

            // Log valid token issue time, token expiration time, and current system time
            Log.d("---VALID_TOKEN_ISSUED", "---iat = $tokenIssuedAtTime (unix seconds)")
            Log.d("---VALID_TOKEN_EXPIRATION", "---exp = $tokenExpirationTime (unix seconds)")
            Log.d("---SYSTEM_TIME", "---now = $currentTime (unix seconds)")
            // Log above in readable format
            Log.d("---VALID_TOKEN_ISSUED", "---iat (readable) = ${Instant.ofEpochSecond(tokenIssuedAtTime ?: 0)}")
            Log.d("---VALID_TOKEN_EXPIRATION", "---exp (readable) = ${Instant.ofEpochSecond(tokenExpirationTime ?: 0)}")
            Log.d("---SYSTEM_TIME", "---now (readable) = ${Instant.ofEpochSecond(currentTime)}")

            // Go to DocumentManagementActivity
            DocumentManagementActivity::class.java
        } else {
            // Log invalid token issue time, token expiration time, and current system time
            Log.d("---INVALID_TOKEN_ISSUED", "---iat = $tokenIssuedAtTime (unix seconds)")
            Log.d("---INVALID_TOKEN_EXPIRATION", "---exp = $tokenExpirationTime (unix seconds)")
            Log.d("---SYSTEM_TIME", "---now = $currentTime (unix seconds)")
            // Log above in readable format
            Log.d("---INVALID_TOKEN_ISSUED", "---iat (readable) = ${Instant.ofEpochSecond(tokenIssuedAtTime ?: 0)}")
            Log.d("---INVALID_TOKEN_EXPIRATION", "---exp (readable) = ${Instant.ofEpochSecond(tokenExpirationTime ?: 0)}")
            Log.d("---SYSTEM_TIME", "---now (readable) = ${Instant.ofEpochSecond(currentTime)}")

            // Go to LoginActivity
            LoginActivity::class.java
        }

        // Delay to show splash for ~1 second
        window.decorView.postDelayed({
            startActivity(Intent(this, nextActivity))
            finish()
        }, 1000) // Delay in milliseconds
    }
}