package edu.bhcc.cho.noteserver.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONObject

// Example Signup function using Volley:
class UserRepository(private val context: Context) {

    fun signUpUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "http://10.0.2.2:8080/auth/signup"   // Replace with your server URL

        val requestBody = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("first_name", firstName)
            put("last_name", lastName)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response -> onSuccess(response) },
            { error -> onError(error.message ?: "Signup failed") }
        )

        VolleySingleton.Companion.getInstance(context).addToRequestQueue(request)
    }
}