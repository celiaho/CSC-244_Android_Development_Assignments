package edu.bhcc.cho.noteserver.data.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.noteserver.data.model.LoginRequest
import edu.bhcc.cho.noteserver.data.model.SignupRequest
import edu.bhcc.cho.noteserver.utils.SessionManager
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONObject

class AuthApiService(private val context: Context) {
    private val baseUrl = "http://10.0.2.2:8080"
    private val requestQueue = VolleySingleton.getInstance(context).requestQueue

    /**
     * Logs in a user and returns the JWT token string.
     */
    fun loginUser(
        request: LoginRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/auth/login"
        val json = JSONObject().apply {
            put("email", request.email)
            put("password", request.password)
        }

        val req = JsonObjectRequest(Request.Method.POST, url, json,
            { response ->
                val token = response.optString("token", "")
                if (token.isNotEmpty()) onSuccess(token)
                else onError("Missing token in response")
            },
            { error -> onError(error.message ?: "Login failed") }
        )

        requestQueue.add(req)
    }

    /**
     * Signs up a user and returns the full response object.
     */
    fun signupUser(
        request: SignupRequest,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/auth/signup"
        val json = JSONObject().apply {
            put("email", request.email)
            put("password", request.password)
            put("first_name", request.firstName)
            put("last_name", request.lastName)
            put("extra", request.extra)
        }

        val req = JsonObjectRequest(Request.Method.POST, url, json,
            { response -> onSuccess(response) },
            { error -> onError(error.message ?: "Signup failed") }
        )

        requestQueue.add(req)
    }

    /**
     * Uses OTP to reset a user's password.
     */
    fun resetPassword(
        email: String,
        newPassword: String,
        otp: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/auth/reset-password"
        val json = JSONObject().apply {
            put("email", email)
            put("new_password", newPassword)
            put("otp", otp)
        }

        val req = JsonObjectRequest(Request.Method.POST, url, json,
            { onSuccess() },
            { error -> onError(error.message ?: "Reset failed") }
        )

        requestQueue.add(req)
    }

    /**
     * Fetches the current user's profile using JWT in header.
     */
    fun getMyProfile(
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/profiles/me"
        val req = object : JsonObjectRequest(Method.GET, url, null,
            { response -> onSuccess(response) },
            { error -> onError(error.message ?: "Profile fetch failed") }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${SessionManager(context).getToken()}")
            }
        }

        requestQueue.add(req)
    }
}