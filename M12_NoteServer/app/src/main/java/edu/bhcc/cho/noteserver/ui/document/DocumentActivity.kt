package edu.bhcc.cho.noteserver.ui.document

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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

class DocumentActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var apiService: DocumentApiService

    private var documentId: String? = null // UUID from server
    private var isNewDoc = false

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

        // Load data passed from previous screen
        documentId = intent.getStringExtra("DOCUMENT_ID") // if passed in
        val docTitle = intent.getStringExtra("DOCUMENT_TITLE")
        val docContent = intent.getStringExtra("DOCUMENT_CONTENT")
        isNewDoc = documentId == null

        if (!isNewDoc) {
            titleEditText.setText(docTitle ?: "")
            contentEditText.setText(docContent ?: "")
        }

        // Handle toolbar buttons
        findViewById<ImageButton>(R.id.icon_new_doc).setOnClickListener {
            startActivity(Intent(this, DocumentActivity::class.java).apply {
                putExtra("newDoc", true)
            })
        }

        findViewById<ImageButton>(R.id.icon_open_folder).setOnClickListener {
            startActivity(Intent(this, DocumentManagementActivity::class.java))
        }

        findViewById<ImageButton>(R.id.icon_save).setOnClickListener {
            saveDocument()
        }

        findViewById<ImageButton>(R.id.icon_share).setOnClickListener {
            // TODO: implement actual popup
            Toast.makeText(this, "Share popup opened", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.icon_delete).setOnClickListener {
            confirmDelete()
        }

        findViewById<ImageButton>(R.id.icon_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<ImageButton>(R.id.icon_logout).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * Save the document using either POST (create) or PUT (update) depending on presence of documentId.
     */
    private fun saveDocument() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show()
            return
        }

        // Construct JSON payload
        val json = JSONObject().apply {
            put("title", title)
            put("body", content)
        }
        val wrapper = JSONObject().apply {
            put("content", json)
        }

        if (documentId == null) {
            // Create new document
            apiService.createDocument(
                content = wrapper,
                onSuccess = {
                    Toast.makeText(this, "New document saved.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DocumentManagementActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, "Failed to save document.", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Update existing document
            apiService.updateDocument(
                documentId!!,
                content = wrapper,
                onSuccess = {
                    Toast.makeText(this, "Document updated.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DocumentManagementActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, "Failed to update document.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    /**
     * Ask user to confirm document deletion.
     */
    private fun confirmDelete() {
        if (documentId == null) {
            Toast.makeText(this, "This document hasn't been saved yet", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Document deleted", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DocumentManagementActivity::class.java))
                finish()
            },
            onError = {
                Toast.makeText(this, "Failed to delete document", Toast.LENGTH_SHORT).show()
            }
        )
    }
}