package edu.bhcc.cho.noteserver.data.network

import android.content.Context
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import edu.bhcc.cho.noteserver.data.model.Document
import edu.bhcc.cho.noteserver.utils.SessionManager
import edu.bhcc.cho.noteserver.utils.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

/**
 * Handles all document CRUD and content-query operations.
 */
class DocumentApiService(context: Context) {
    private val requestQueue = VolleySingleton.getInstance(context).requestQueue
    private val baseUrl = "http://10.0.2.2:8080"
    private val sessionManager = SessionManager(context)

    private fun authHeaders(): Map<String, String> =
        mapOf("Authorization" to "Bearer ${sessionManager.getToken()}")

    fun createDocument(
        content: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents"
        val request = object : JsonObjectRequest(Method.POST, url, content, onSuccess, onError) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    fun updateDocument(
        documentId: String,
        content: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId"
        val request = object : JsonObjectRequest(Method.PUT, url, content, onSuccess, onError) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    fun deleteDocument(
        documentId: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId"
        val request = object : StringRequest(Method.DELETE, url, onSuccess, onError) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    fun getDocuments(
        onSuccess: (List<Document>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents"
        val request = object : JsonArrayRequest(Method.GET, url, null,
            { array -> onSuccess(parseList(array)) },
            onError
        ) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    private fun parseList(array: JSONArray): List<Document> {
        val list = mutableListOf<Document>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(Document(
                id = obj.getString("id"),
                ownerId = obj.getString("owner_id"),
                creationDate = obj.getString("creation_date"),
                lastModifiedDate = obj.getString("last_modified_date"),
                title = obj.optString("title", ""),
                content = obj.getJSONObject("content").toString()
            ))
        }
        return list
    }

    fun getAllUsers(
        onSuccess: (List<UserProfile>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/profiles"
        val request = object : JsonArrayRequest(Method.GET, url, null,
            { response ->
                try {
                    val users = mutableListOf<UserProfile>()
                    for (i in 0 until response.length()) {
                        val obj = response.getJSONObject(i)
                        users.add(
                            UserProfile(
                                id = obj.getString("id"),
                                firstName = obj.getString("first_name"),
                                lastName = obj.getString("last_name"),
                                email = obj.getString("email")
                            )
                        )
                    }
                    onSuccess(users)
                } catch (e: Exception) {
                    onError("Error parsing users: ${e.message}")
                }
            },
            { error -> onError(error.message ?: "Error fetching users") }
        ) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    fun getSharedUsers(
        documentId: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/shared"
        val request = object : JsonArrayRequest(Method.GET, url, null,
            { response ->
                try {
                    val sharedIds = mutableListOf<String>()
                    for (i in 0 until response.length()) {
                        sharedIds.add(response.getString(i))
                    }
                    onSuccess(sharedIds)
                } catch (e: Exception) {
                    onError("Error parsing shared user IDs: ${e.message}")
                }
            },
            { error -> onError(error.message ?: "Error fetching shared users") }
        ) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    fun shareDocumentWithUser(
        documentId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/share/$userId"
        val request = object : StringRequest(Method.POST, url, { onSuccess() }, { error ->
            onError(error.message ?: "Error sharing document")
        }) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    fun unshareDocumentWithUser(
        documentId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId/share/$userId"
        val request = object : StringRequest(Method.DELETE, url, { onSuccess() }, { error ->
            onError(error.message ?: "Error unsharing document")
        }) {
            override fun getHeaders(): MutableMap<String, String> = authHeaders().toMutableMap()
        }
        requestQueue.add(request)
    }

    // Add this data class inside or outside the service if not already available
    data class UserProfile(val id: String, val firstName: String, val lastName: String, val email: String)
}