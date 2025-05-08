package edu.bhcc.cho.noteserver.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Handles storing and retrieving the authentication token and session-related info.
 */
class SessionManager(context: Context) {
    // SharedPreferences file name
    private val prefsName = "GottNotesSession"
    private val prefs: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    // Key to store the auth token
    private val KEY_AUTH_TOKEN = "auth_token"

    /**
     * Saves the auth token to shared preferences.
     *
     * @param token The JWT token returned from the server.
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Retrieves the stored auth token.
     *
     * @return The token, or null if not found.
     */
    fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Saves the user ID to shared preferences.
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString("user_id", userId).apply()
    }

    /**
     * Gets the user ID from shared preferences.
     */
    fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }

    /**
     * Deletes the stored auth token (e.g. on logout).
     */
    fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    /**
     * Checks if a token exists (useful to check if user is logged in).
     */
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}