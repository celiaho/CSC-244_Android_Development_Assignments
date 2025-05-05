package edu.bhcc.cho.note_takingserverapp.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.note_takingserverapp.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

class SharingApiService(context: Context) {
    private val requestQueue = VolleySingleton.getInstance(context).requestQueue
    private val baseUrl = "http://10.0.2.2:8080"
    private val tag = "SharingApiService"

    /**
     * Gets the list of user IDs the document is shared with.
     *
     * @param documentId The ID of the document.
     * @param onSuccess Callback function to handle a successful response, providing the list of user IDs.
     * @param onError Callback function to handle an error during the request.
     */
    fun getSharedUsers(
        documentId: String,
        onSuccess: (List<String>) -> Unit,
        onError: (com.android.volley.VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares"

        val jsonObjectRequest = JsonObjectRequest( // Expecting a JSON object, not array.
            Request.Method.GET, url, null,
            { response ->
                try {
                    //  The server returns a JSON object with a "sharedWith" key
                    val sharedWithArray = response.getJSONArray("sharedWith")
                    val sharedUserIds = mutableListOf<String>()
                    for (i in 0 until sharedWithArray.length()) {
                        sharedUserIds.add(sharedWithArray.getString(i))
                    }
                    onSuccess(sharedUserIds)
                } catch (e: Exception) {
                    onError(com.android.volley.VolleyError("Error parsing JSON: ${e.message}"))
                }
            },
            { error ->
                onError(error)
            }
        )
        jsonObjectRequest.tag = tag
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Sets (replaces) the list of users the document is shared with.
     *
     * @param documentId The ID of the document.
     * @param sharedWith A list of user IDs to share the document with.
     * @param onSuccess Callback function to handle a successful response.
     * @param onError Callback function to handle an error during the request.
     */
    fun setSharedUsers(
        documentId: String,
        sharedWith: List<String>,
        onSuccess: (JSONObject) -> Unit, //  Or Unit if the server returns an empty response
        onError: (com.android.volley.VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares"

        //  Construct the JSON request body.  The key is "sharedWith" and the value is a JSON array.
        val jsonArray = JSONArray()
        sharedWith.forEach { userId ->
            jsonArray.put(userId)
        }
        val jsonRequest = JSONObject().apply {
            put("sharedWith", jsonArray)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, jsonRequest,
            { response ->
                onSuccess(response)
            },
            { error ->
                onError(error)
            }
        )
        jsonObjectRequest.tag = tag
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Shares a document with a single user.
     *
     * @param documentId The ID of the document.
     * @param profileId The ID of the user to share with.
     * @param onSuccess Callback function to handle a successful response.
     * @param onError Callback function to handle an error during the request.
     */
    fun shareWithUser(
        documentId: String,
        profileId: String,
        onSuccess: (JSONObject) -> Unit,  // Or Unit
        onError: (com.android.volley.VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares/$profileId"

        // PUT with an empty JSON object as the body.  Check server API docs for expected body.
        val jsonRequest = JSONObject()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, jsonRequest,
            { response ->
                onSuccess(response)
            },
            { error ->
                onError(error)
            }
        )
        jsonObjectRequest.tag = tag
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Stops sharing a document with a single user.
     *
     * @param documentId The ID of the document.
     * @param profileId The ID of the user to stop sharing with.
     * @param onSuccess Callback function to handle a successful response.
     * @param onError Callback function to handle an error during the request.
     */
    fun stopSharingWithUser(
        documentId: String,
        profileId: String,
        onSuccess: () -> Unit, // DELETE often has empty body
        onError: (com.android.volley.VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares/$profileId"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.DELETE, url, null, // No body for DELETE
            { _ ->
                onSuccess()
            },
            { error ->
                onError(error)
            }
        )
        jsonObjectRequest.tag = tag
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Cancels all pending requests with this tag.
     */
    fun cancelRequests() {
        requestQueue.cancelAll(tag)
    }
}