package edu.bhcc.cho.noteserver.ui.document

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.network.DocumentApiService
import edu.bhcc.cho.noteserver.ui.settings.SettingsActivity
import edu.bhcc.cho.noteserver.ui.auth.LoginActivity
import org.json.JSONObject
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit

/**
 * Handles document creation, editing, autosaving, and deletion.
 * Also manages loading from local cache and syncing with server.
 */
class DocumentActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var apiService: DocumentApiService

    private var documentId: String? = null // UUID from server
    private var isNewDoc = false

    // For autosaving
    private val autosaveHandler = Handler(Looper.getMainLooper())
    private var isAutosaveRunning = false // flag to control autosave looping
    private val autosaveRunnable = object : Runnable {
        /**
         * Runnable that triggers autosave every 5 seconds while the activity is visible.
         */
        override fun run() {
            if (isAutosaveRunning) {
                saveDocument()
                autosaveHandler.postDelayed(this, 5000) // Repeat every 5 seconds
            }
        }
    }

    /**
     * Called when the DocumentActivity is created to nitialize the activity UI and document state.
     *  Handles all toolbar button setup, determines if the document is new or existing,
     *  and loads document content either from local cache (new) or server (existing).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        // Highlight New Document icon
        findViewById<ImageButton>(R.id.icon_new_doc)
            .setColorFilter(ContextCompat.getColor(this, R.color.orange))

        // Initialize views and API
        titleEditText = findViewById(R.id.document_title)
        contentEditText = findViewById(R.id.document_content)
        apiService = DocumentApiService(this)

        Log.d("---DOCUMENT_PAGE_LOADED", "---DOCUMENT_PAGE_LOADED")

        // *Handle toolbar clicks*
        // Handle New Document Button
        findViewById<ImageButton>(R.id.icon_new_doc).setOnClickListener {
            Log.d("---NEW_DOCUMENT_BUTTON_CLICKED", "---NEW_DOCUMENT_BUTTON_CLICKED")
            startActivity(Intent(this, DocumentActivity::class.java).apply {
                putExtra("newDoc", true)
            })
        }
        // Handle Document Management Button
        findViewById<ImageButton>(R.id.icon_open_folder).setOnClickListener {
            Log.d("---DOCUMENT_MANAGEMENT_BUTTON_CLICKED", "---DOCUMENT_MANAGEMENT_BUTTON_CLICKED")
            startActivity(Intent(this, DocumentManagementActivity::class.java))
        }
        // Handle Save Document Button
        findViewById<ImageButton>(R.id.icon_save).setOnClickListener {
            Log.d("---SAVE_DOCUMENT_BUTTON_CLICKED", "---SAVE_DOCUMENT_BUTTON_CLICKED")
            saveDocument()
        }
        // Handle Share Document Button
        findViewById<ImageButton>(R.id.icon_share).setOnClickListener {
            Log.d("---SHARE_DOCUMENT_BUTTON_CLICKED", "---SHARE_DOCUMENT_BUTTON_CLICKED")

            if (documentId.isNullOrEmpty()) {
                Toast.makeText(this, "Please save the document before sharing.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(this, DocumentSharePopupActivity::class.java).apply {
                putExtra("DOCUMENT_ID", documentId)
            }
            startActivity(intent)
        }
        // Handle Delete Document Button
        findViewById<ImageButton>(R.id.icon_delete).setOnClickListener {
            Log.d("---DELETE_DOCUMENT_BUTTON_CLICKED", "---DELETE_DOCUMENT_BUTTON_CLICKED")
            confirmDelete()
        }
        // Handle Settings Button
        findViewById<ImageButton>(R.id.icon_settings).setOnClickListener {
            Log.d("---SETTINGS_BUTTON_CLICKED", "---SETTINGS_BUTTON_CLICKED")
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        // Handle Logout Button
        findViewById<ImageButton>(R.id.icon_logout).setOnClickListener {
            Log.d("---LOGOUT_BUTTON_CLICKED", "---LOGOUT_BUTTON_CLICKED")
            getSharedPreferences(
                "DocumentCache",
                MODE_PRIVATE
            ).edit { clear() } // Clear local cache on logout
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Load any data passed from previous screen
        documentId = intent.getStringExtra("DOCUMENT_ID")
        isNewDoc = documentId == null

        if (isNewDoc) {
            // Load from local cache
            val prefs = getSharedPreferences("DocumentCache", MODE_PRIVATE)
            val cachedTitle = prefs.getString("cachedTitle", "")
            val cachedContent = prefs.getString("cachedContent", "")
            titleEditText.setText(cachedTitle)
            contentEditText.setText(cachedContent)
        } else {
            // Load document fresh from server using document ID
            apiService.getDocumentById(
                documentId = documentId!!,
                onSuccess = { doc ->
                    titleEditText.setText(doc.title)
                    contentEditText.setText(doc.content)
                },
                onError = {
                    Log.e("---DOCUMENT_LOAD_FAILED", "Failed to load document from server")
                    Toast.makeText(this, "Could not load document", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    /**
     * Starts the autosave loop when the activity becomes visible.
     * Autosave runs every 5 seconds to prevent data loss.
     */
    override fun onResume() {
        super.onResume()
        isAutosaveRunning = true
        autosaveHandler.post(autosaveRunnable)
    }

    /**
     * Stops the autosave loop when the activity is no longer in the foreground.
     * This prevents unnecessary background work and avoids memory leaks
     * when the user navigates away from the screen.
     */
    override fun onPause() {
        super.onPause()
        isAutosaveRunning = false
        autosaveHandler.removeCallbacks(autosaveRunnable)
    }

    /**
     * Saves the current document to the backend.
     * - If document is new, it sends a POST request to create.
     * - If editing, it sends a PUT request to update.
     * Also caches content locally and signals result to parent activity.
     */
    private fun saveDocument() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        // Skip autosave if it's a new document and both title and content are empty
        if (documentId == null && title.isEmpty() && content.isEmpty()) {
            Log.d("---AUTOSAVE_SKIPPED", "---AUTOSAVE SKIPPED: empty new document")
            return // Exit saveDocument() without running remaining code
        }

        // Construct JSON payload
        val json = JSONObject().apply {
            put("title", title)
            put("body", content)
        }
        val wrapper = JSONObject().apply {
            put("content", json)
        }

        // Save to local cache before uploading to server
        val prefs = getSharedPreferences("DocumentCache", MODE_PRIVATE)
        prefs.edit().apply {
            putString("cachedTitle", title)
            putString("cachedContent", content)
            apply()
            Log.d("---DOCUMENT_SAVED_TO_CACHE", "DOCUMENT SAVED TO LOCAL CACHE at ${java.time.Instant.now()}"
            )
        }

        if (documentId == null) {
            // Create new document
            apiService.createDocument(
                content = wrapper,
                onSuccess = { response ->
                    documentId = response.optString("id", "") // Save doc ID so future saves are updates
                    Log.d("---NEW_DOCUMENT_SAVED", "---NEW DOCUMENT SAVED at ${java.time.Instant.now()}")
                    Log.d("---DOCUMENT_RESPONSE", "Response: $response") // Log full response
                    Toast.makeText(this, "New document saved.", Toast.LENGTH_SHORT).show()
                    prefs.edit { clear() } // Clear cache after server save
                    // Signal refresh
                    val resultIntent = Intent()
                    resultIntent.putExtra("REFRESH_NEEDED", true)
                    setResult(RESULT_OK, resultIntent)
                },
                onError = {
                    Log.d("---NEW_DOCUMENT_SAVE_FAILED", "---NEW_DOCUMENT_SAVE_FAILED")
                    Toast.makeText(this, "Failed to save document.", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Update existing document
            apiService.updateDocument(
                documentId!!,
                content = wrapper,
                onSuccess = {
                    Log.d("---DOCUMENT_UPDATED", "---DOCUMENT UPDATED at ${java.time.Instant.now()}")
                    prefs.edit { clear() } // Clear cache after server update
                    // Signal refresh
                    val resultIntent = Intent()
                    resultIntent.putExtra("REFRESH_NEEDED", true)
                    setResult(RESULT_OK, resultIntent)
                },
                onError = {
                    Log.d("---DOCUMENT_UPDATE_FAILED", "---DOCUMENT_UPDATE_FAILED")
                    Toast.makeText(this, "Failed to update document.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    /**
     * Ask user to confirm document deletion.
     */
    private fun confirmDelete() {
        if (documentId == null) { /// @todo PROBLEM HERE
            Log.d("---DOCUMENT_DELETE_FAILED", "---DOCUMENT DELETE FAILED: Document hasn't been saved yet")
            Toast.makeText(this, "Cannot delete document because it hasn't been saved yet", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this document?")
            .setPositiveButton("Delete") { _, _ -> deleteDocument() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Delete the document using DELETE request.
     */
    private fun deleteDocument() {
        val id = documentId ?: return
        apiService.deleteDocument(
            documentId = id,
            onSuccess = {
                Log.d("---DOCUMENT_DELETED", "---DOCUMENT_DELETED")
                Toast.makeText(this, "Document deleted", Toast.LENGTH_LONG).show()
                getSharedPreferences("DocumentCache", MODE_PRIVATE).edit { clear() } // Clear cache after server deletion
                // Signal refresh
                val resultIntent = Intent()
                resultIntent.putExtra("REFRESH_NEEDED", true)
                setResult(RESULT_OK, resultIntent)
                // Go to DocumentManagementActivity
                startActivity(Intent(this, DocumentManagementActivity::class.java))
                finish()
            },
            onError = {
                Log.d("---DOCUMENT_DELETE_FAILED", "---DOCUMENT_DELETE_FAILED")
                Toast.makeText(this, "Failed to delete document", Toast.LENGTH_SHORT).show()
            }
        )
    }
}