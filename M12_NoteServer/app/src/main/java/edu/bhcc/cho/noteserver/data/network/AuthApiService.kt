package edu.bhcc.cho.noteserver.data.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.noteserver.data.model.SignupRequest
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONObject

class AuthApiService(context: Context) {
    private val requestQueue: RequestQueue = VolleySingleton.Companion.getInstance(context).requestQueue
    // Replace with your computer's IP; use 10.0.2.2 for emulator.
    private val baseUrl = "http://10.0.2.2:8080"

    /**
     * Function to register a new user using the /auth/auth endpoint.
     *
     * @param signupRequest The data class containing the user's registration information.
     * @param onSuccess Callback function to execute on successful registration.  Returns the JSON response.
     * @param onError Callback function to execute on registration failure. Returns the error message string.
     */
    fun signupUser(
        signupRequest: SignupRequest,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/auth/auth"
        val jsonRequest = JSONObject().apply {
            put("email", signupRequest.email)
            put("password", signupRequest.password)
            put("first_name", signupRequest.firstName)
            put("last_name", signupRequest.lastName)
            put("extra", signupRequest.extra)
        }

        // Create JsonObjectRequest for requests that send and/or receive JSON.
        val request = JsonObjectRequest(
            // Specify HTTP method, endpoint URL, JSON object to send in the request body
            Request.Method.POST, url, jsonRequest,
            // Listener for successful responses; execute the success callback
            // Response.Listener { response -> onSuccess(response) } // Equivalent to line below
            { response -> onSuccess(response) },
            // Listener for error responses; execute the error callback with an error message
            // Response.ErrorListener { response -> onSuccess(response) } // Equivalent to line below
            { error -> onError(error.message ?: "Signup error") }
        )

        // Add the request to the RequestQueue to send the request to the server
        requestQueue.add(request)
    }

    // Add other functions here for login, logout, etc.
}