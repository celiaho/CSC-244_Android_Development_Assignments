package edu.bhcc.cho.noteserver.utils

import android.util.Base64
import org.json.JSONObject

/**
 * Utility object for working with JWT (JSON Web Tokens).
 * Provides methods to decode JWT payloads and extract token expiration times.
 */
object JwtUtils {
    fun getIssuedAtTime(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payload = JSONObject(String(payloadBytes))

            if (payload.has("iat")) payload.getLong("iat") else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extracts the `exp` (expiration timestamp) from a JWT token.
     *
     * @param token The JWT token string (in format `header.payload.signature`).
     * @return The expiration time in seconds since epoch (Unix timestamp),
     *         or null if decoding fails or `exp` is not found.
     */
    fun getExpirationTime(token: String): Long? {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            // Decode the payload (second part of the JWT)
            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payload = JSONObject(String(payloadBytes))

            return if (payload.has("exp")) {
                payload.getLong("exp")
            } else {
                null
            }
        } catch (e: Exception) {
            // Return null if decoding or parsing fails
            return null
        }
    }

    /**
     * Checks whether a given JWT token is expired compared to the current time.
     *
     * @param token The JWT token string.
     * @return True if the token is expired or invalid, false if still valid.
     */
    fun isTokenExpired(token: String): Boolean {
        val exp = getExpirationTime(token) ?: return true

//        val currentTime = System.currentTimeMillis() / 1000 // Original code, replaced with below as workaround

        // TEMP PATCH: Emulator or DocServer has a 1-hour time skew.
        // Add 3600 seconds (1 hour) to system clock to match server's apparent validation time.
        // ⚠️ REMOVE BEFORE FINAL SUBMISSION or if server is fixed.
        val currentTime = System.currentTimeMillis() / 1000 + 3600

        return currentTime >= exp
    }

    //// WORKAROUND HELPER FUNCTION TEST CODE for LoginActivity
    fun getUserId(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payload = JSONObject(String(payloadBytes))
            payload.optString("id", null) // or "sub" or whatever the server uses
        } catch (e: Exception) {
            null
        }
    }
}