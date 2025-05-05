package edu.bhcc.cho.note_takingserverapp.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.note_takingserverapp.models.SignupRequest
import org.json.JSONObject
import com.android.volley.toolbox.Volley
import com.android.volley.VolleyError
import com.android.volley.Response
import edu.bhcc.cho.note_takingserverapp.VolleySingleton

class AuthApiService(context: Context) {
    private val requestQueue: RequestQueue = VolleySingleton.getInstance(context).requestQueue
    // Replace with your computer's IP; use 10.0.2.2 for emulator.
    private val baseUrl = "http://10.0.2.2:8080"

    /**
     * Function to register a new user using the /auth/signup endpoint.
     *
     * @param signupRequest The data class containing the user's registration information.
     * @param onSuccess Callback function to execute on successful registration.  Returns the JSON response.
     * @param onError Callback function to execute on registration failure. Returns the VolleyError.
     */
    fun signupUser(
        signupRequest: SignupRequest,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/auth/signup"  // Construct the full URL
        val jsonRequest = JSONObject().apply { // Create a JSON object for the request body
            put("email", signupRequest.email)
            put("password", signupRequest.password) // min 8 char
            put("firstName", signupRequest.firstName)
            put("lastName", signupRequest.lastName)
            // Add other fields from SignupRequest if necessary, according to your API
        }

        // Create JsonObjectRequest for requests that send and/or receive JSON.
        val request = JsonObjectRequest(
            Request.Method.POST, // Specify the HTTP method
            url,                  // The URL to the endpoint
            jsonRequest,          // The JSON object to send in the request body
            Response.Listener { response ->  // Listener for successful responses
                onSuccess(response)       // Execute the success callback
            },
            Response.ErrorListener { error -> // Listener for error responses
                onError(error)         // Execute the error callback
            }
        )

        // Add the request to the RequestQueue to send the request to the server
        requestQueue.add(request)
    }

    // Add other functions here for login, logout, etc.
}