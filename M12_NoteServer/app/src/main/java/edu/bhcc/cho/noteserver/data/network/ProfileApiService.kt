package edu.bhcc.cho.noteserver.data.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.noteserver.data.model.Profile
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.iterator

class ProfileApiService(context: Context) {
    private val requestQueue = VolleySingleton.Companion.getInstance(context).requestQueue
    private val baseUrl = "http://10.0.2.2:8080"
    private val tag = "ProfileApiService" // Tag for Volley requests, can be used for cancellation

    /**
     * Retrieves the user's own profile information.
     *
     * @param onSuccess Callback function to handle a successful response, providing the Profile data.
     * @param onError Callback function to handle an error during the request.
     */
    fun getProfile(onSuccess: (Profile) -> Unit, onError: (VolleyError) -> Unit) {
        val url = "$baseUrl/profiles/me"

        // Create a JsonObjectRequest for fetching profile data.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, // No request body for GET
            { response ->
                // Parse the JSON response into a Profile object.
                try {
                    val profile = parseProfileJson(response)
                    onSuccess(profile) // Invoke the success callback with the parsed data.
                } catch (e: Exception) {
                    // Handle JSON parsing errors.  Wrap in VolleyError for consistency.
                    onError(VolleyError("Error parsing JSON: ${e.message}"))
                }
            },
            { error ->
                // Handle network errors, server errors, etc.
                onError(error)
            }
        )

        jsonObjectRequest.tag = tag // Set a tag for the request.
        requestQueue.add(jsonObjectRequest) // Add the request to the queue.
    }

    /**
     * Updates the user's own profile information.
     *
     * @param updatedProfile The Profile object containing the updated data.
     * @param onSuccess Callback function to handle a successful response.
     * @param onError Callback function to handle an error during the request.
     */
    fun updateProfile(
        updatedProfile: Profile,
        onSuccess: (JSONObject) -> Unit, // Or a Profile if your server returns the updated profile
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/profiles/me"

        // Create a JSONObject from the updatedProfile data.
        val jsonRequest = JSONObject().apply {
            put("firstName", updatedProfile.firstName)
            put("lastName", updatedProfile.lastName)

            // Handle the 'extra' field, which is a Map<String, Any>
            if (updatedProfile.extra != null) {
                val extraJson = JSONObject()
                for ((key, value) in updatedProfile.extra) {
                    extraJson.put(key, value) // Put each key-value pair into the JSONObject
                }
                put("extra", extraJson)
            }
        }

        // Create a JsonObjectRequest for updating the profile.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, jsonRequest,
            { response ->
                onSuccess(response) // Invoke the success callback.
            },
            { error ->
                onError(error) // Invoke the error callback.
            }
        )
        jsonObjectRequest.tag = tag
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Deletes the user's own profile.
     *
     * @param onSuccess Callback function to handle a successful response.
     * @param onError Callback function to handle an error during the request.
     */
    fun deleteProfile(onSuccess: () -> Unit, onError: (VolleyError) -> Unit) {
        val url = "$baseUrl/profiles/me"

        // Create a JsonObjectRequest for deleting the profile.  DELETE usually doesn't have a body.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.DELETE, url, null, // No body
            { _ ->  //  Successful DELETE responses often have empty bodies.
                onSuccess() // Invoke the success callback.
            },
            { error ->
                onError(error) // Invoke the error callback.
            }
        )
        jsonObjectRequest.tag = tag
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Searches for user profiles.  This is used for sharing documents.
     *
     * @param query The search query string.
     * @param onSuccess Callback function to handle a successful response, providing a list of Profile data.
     * @param onError Callback function to handle an error during the request.
     */
    fun searchProfiles(
        query: String,
        onSuccess: (List<Profile>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/profiles?query=$query" //  Include the query parameter

        val jsonArrayRequest =
            JsonArrayRequest( // Use JsonArrayRequest since the server returns an array
                Request.Method.GET, url, null,
                { response ->
                    try {
                        val profiles = parseProfileListJson(response) //use the new function
                        onSuccess(profiles)
                    } catch (e: Exception) {
                        onError(VolleyError("Error parsing JSON: ${e.message}"))
                    }
                },
                { error ->
                    onError(error)
                }
            )
        jsonArrayRequest.tag = tag
        requestQueue.add(jsonArrayRequest)
    }

    /**
     * Helper function to parse a single Profile JSON object.
     */
    private fun parseProfileJson(jsonObject: JSONObject): Profile {
        // Parse the 'extra' field as a Map<String, Any>
        val extraMap = if (jsonObject.has("extra")) {
            val extraJson = jsonObject.getJSONObject("extra")
            val map = mutableMapOf<String, Any>()
            val keys = extraJson.names() ?: JSONArray() // Handle null case
            for (i in 0 until keys.length()) {
                val key = keys.getString(i)
                val value = extraJson.get(key)
                // Convert primitive types from JSONObject to Kotlin types
                val kotlinValue: Any = when (value) {
                    is String -> value
                    is Int -> value
                    is Double -> value
                    is Boolean -> value
                    is Long -> value
                    is JSONObject -> value.toString()  // Convert JSONObject to String
                    is JSONArray -> value.toString() // Convert JSONArray to String
                    else -> value
                }
                map[key] = kotlinValue
            }
            map.toMap() // Convert to immutable map
        } else {
            emptyMap()
        }

        return Profile(
            id = jsonObject.getString("id"),
            email = jsonObject.getString("email"),
            firstName = jsonObject.getString("firstName"),
            lastName = jsonObject.getString("lastName"),
            creationDate = jsonObject.getString("creationDate"),
            lastModifiedDate = jsonObject.getString("lastModifiedDate"),
            extra = extraMap,
        )
    }

    /**
     * Helper function to parse a list of Profile JSON objects.
     */
    private fun parseProfileListJson(jsonArray: JSONArray): List<Profile> {
        val profiles = mutableListOf<Profile>()
        for (i in 0 until jsonArray.length()) {
            val profileJson = jsonArray.getJSONObject(i)
            profiles.add(parseProfileJson(profileJson))
        }
        return profiles
    }

    /**
     * Cancels all pending requests with this tag.  Good practice to call in Activity/Fragment onDestroy.
     */
    fun cancelRequests() {
        requestQueue.cancelAll(tag)
    }
}