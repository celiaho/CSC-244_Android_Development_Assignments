package edu.bhcc.cho.note_takingserverapp.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import edu.bhcc.cho.note_takingserverapp.VolleySingleton
import edu.bhcc.cho.note_takingserverapp.models.Document
import org.json.JSONObject
import org.json.JSONArray
import com.android.volley.VolleyError
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.Charset

class DocumentApiService(context: Context) {

    private val requestQueue = VolleySingleton.getInstance(context).requestQueue
    private val baseUrl = "http://10.0.2.2:8080"
    private val tag = "DocumentApiService" // Tag to use for Volley requests, for cancellation

    /**
     * Creates a new document on the server.
     *
     * @param content The content of the document as a JSONObject.
     * @param onSuccess Callback for a successful document creation, returns the created document as JSONObject.
     * @param onError Callback for handling errors during the request.
     */
    fun createDocument(
        content: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents"

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, content, // Use the provided content JSONObject
            { response ->
                onSuccess(response) // Pass the whole JSONObject
            },
            { error ->
                onError(error)
            }
        )
        jsonRequest.tag = tag
        requestQueue.add(jsonRequest)
    }

    /**
     * Retrieves all documents for the current user from the server.
     *
     * @param onSuccess Callback for a successful retrieval, providing a list of Document objects.
     * @param onError Callback for handling errors during the request.
     */
    fun getDocuments(
        onSuccess: (List<Document>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val documents = parseDocumentList(response) // Use helper function
                    onSuccess(documents)
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
     * Retrieves a single document from the server by its ID.
     *
     * @param documentId The ID of the document to retrieve.
     * @param onSuccess Callback for a successful retrieval, providing the Document object.
     * @param onError Callback for handling errors during the request.
     */
    fun getDocument(
        documentId: String,
        onSuccess: (Document) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val document = parseDocument(response) // Use helper
                    onSuccess(document)
                } catch (e: Exception) {
                    onError(VolleyError("Error parsing JSON: ${e.message}"))
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
     * Updates an existing document on the server.
     *
     * @param documentId The ID of the document to update.
     * @param content The updated content of the document as a JSONObject.
     * @param onSuccess Callback for a successful update. Returns the updated document.
     * @param onError Callback for handling errors during the request.
     */
    fun updateDocument(
        documentId: String,
        content: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, content, // Use the provided content
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
     * Deletes a document from the server.
     *
     * @param documentId The ID of the document to delete.
     * @param onSuccess Callback for a successful deletion.  Returns a string.
     * @param onError Callback for handling errors during the request.
     */
    fun deleteDocument(
        documentId: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = "$baseUrl/documents/$documentId"

        val stringRequest = StringRequest( // Use StringRequest for a simple DELETE
            Request.Method.DELETE, url,
            { response ->
                onSuccess(response) // Return the response
            },
            { error ->
                onError(error)
            }
        )
        stringRequest.tag = tag
        requestQueue.add(stringRequest)
    }

    /**
     * Retrieves documents based on a content query.
     *
     * @param query The content query string.
     * @param onSuccess Callback for a successful retrieval, providing a list of Document objects.
     * @param onError Callback for handling errors during the request.
     */
    fun getDocumentsByQuery(
        query: String,
        onSuccess: (List<Document>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        // URL encode the query parameter
        val encodedQuery = try {
            URLEncoder.encode(query, Charset.defaultCharset().toString())
        } catch (e: UnsupportedEncodingException) {
            onError(VolleyError("Error encoding query: ${e.message}"))
            return // IMPORTANT: Return here to avoid further execution
        }
        val url = "$baseUrl/documents?content_query=$encodedQuery"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val documents = parseDocumentList(response)
                    onSuccess(documents)
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
     * Helper function to parse a single Document JSON object.
     * Handles nested JSON structure for content.
     */
    private fun parseDocument(jsonObject: JSONObject): Document {
        return Document(
            id = jsonObject.getString("id"),
            ownerId = jsonObject.getString("owner_id"),
            creationDate = jsonObject.getString("creation_date"),
            lastModifiedDate = jsonObject.getString("last_modified_date"),
            title = jsonObject.getString("title"),
            content = jsonObject.getJSONObject("content").toString() // Convert the content JSONObject to a string
        )
    }

    /**
     * Helper function to parse a list of Document JSON objects.
     */
    private fun parseDocumentList(jsonArray: JSONArray): List<Document> {
        val documents = mutableListOf<Document>()
        for (i in 0 until jsonArray.length()) {
            val documentJson = jsonArray.getJSONObject(i)
            documents.add(parseDocument(documentJson))
        }
        return documents
    }

    /**
     * Cancels all pending requests with this tag.  Call this in your activity or fragment's onDestroy()
     */
    fun cancelRequests() {
        requestQueue.cancelAll(tag)
    }
}