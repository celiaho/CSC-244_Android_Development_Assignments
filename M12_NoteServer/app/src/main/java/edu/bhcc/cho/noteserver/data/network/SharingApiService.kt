package edu.bhcc.cho.noteserver.data.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import edu.bhcc.cho.noteserver.utils.SessionManager
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

/**
 * Handles network operations for sharing documents with other users.
 */
class SharingApiService(context: Context) {
    private val requestQueue = VolleySingleton.getInstance(context).requestQueue
    private val sessionManager = SessionManager(context)
    private val baseUrl = "http://10.0.2.2:8080"
    private val tag = "SharingApiService"

    /**
     * Gets the list of user IDs a document is shared with.
     */
    fun getSharedUsers(
        documentId: String,
        onSuccess: (List<String>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares"
        val request = object : JsonObjectRequest(Method.GET, url, null, { response ->
            val ids = mutableListOf<String>()
            val sharedArray = response.optJSONArray("sharedWith") ?: JSONArray()
            for (i in 0 until sharedArray.length()) {
                ids.add(sharedArray.getString(i))
            }
            onSuccess(ids)
        }, { error -> onError(error) }) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${sessionManager.getToken()}")
            }
        }
        request.tag = tag
        requestQueue.add(request)
    }

    /**
     * Shares a document with a single user.
     */
    fun shareWithUser(
        documentId: String,
        profileId: String,
        onSuccess: () -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares/$profileId"
        val request = object : JsonObjectRequest(Method.PUT, url, JSONObject(), { _ ->
            onSuccess()
        }, { error -> onError(error) }) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${sessionManager.getToken()}")
            }
        }
        request.tag = tag
        requestQueue.add(request)
    }

    /**
     * Stops sharing a document with a specific user.
     */
    fun unshareWithUser(
        documentId: String,
        profileId: String,
        onSuccess: () -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shares/$profileId"
        val request = object : JsonObjectRequest(Method.DELETE, url, null, { _ ->
            onSuccess()
        }, { error -> onError(error) }) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${sessionManager.getToken()}")
            }
        }
        request.tag = tag
        requestQueue.add(request)
    }

    fun cancelRequests() {
        requestQueue.cancelAll(tag)
    }
}