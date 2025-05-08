package edu.bhcc.cho.noteserver.data.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.noteserver.data.model.LoginRequest
import edu.bhcc.cho.noteserver.data.model.SignupRequest
import edu.bhcc.cho.noteserver.utils.SessionManager
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONObject

/**
 * Handles authentication operations: signup, login.
 */
class AuthApiService(private val context: Context) {
    private val requestQueue = VolleySingleton.getInstance(context).requestQueue
    private val baseUrl = "http://10.0.2.2:8080"
    private val sessionManager = SessionManager(context)

    /**
     * Signs up a new user.
     */
    fun signupUser(
        request: SignupRequest,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/auth/signup"
        val body = JSONObject().apply {
            put("email", request.email)
            put("password", request.password)
            put("first_name", request.firstName)
            put("last_name", request.lastName)
            put("extra", request.extra)
        }

        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, body,
            { response -> onSuccess(response) },
            { error -> onError(error.message ?: "Signup error") }
        )
        requestQueue.add(jsonRequest)
    }

    /**
     * Logs in an existing user and stores token + user ID in SessionManager.
     */
    fun loginUser(
        request: LoginRequest,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/auth/login"
        val body = JSONObject().apply {
            put("email", request.email)
            put("password", request.password)
        }

        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                val token = response.getString("token")
                val userId = response.getString("user_id")
                sessionManager.saveToken(token)
                sessionManager.saveUserId(userId)
                onSuccess(response)
            },
            { error -> onError(error.message ?: "Login failed") }
        )
        requestQueue.add(jsonRequest)
    }
}