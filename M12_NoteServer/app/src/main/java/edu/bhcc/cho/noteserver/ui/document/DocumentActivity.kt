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
import edu.bhcc.cho.noteserver.ui.settings.SettingsActivity
import edu.bhcc.cho.noteserver.ui.auth.LoginActivity

class DocumentActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    private var isNewDoc = false
    private var documentId: String? = null // UUID from server

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        titleEditText = findViewById(R.id.document_title)
        contentEditText = findViewById(R.id.document_content)

        isNewDoc = intent.getBooleanExtra("newDoc", false)
        documentId = intent.getStringExtra("docId") // if passed in

        if (isNewDoc) {
            val newDocIcon = findViewById<ImageButton>(R.id.icon_new_doc)
            newDocIcon.setColorFilter(ContextCompat.getColor(this, R.color.orange))
        }

        findViewById<ImageButton>(R.id.icon_new_doc).setOnClickListener {
            val intent = Intent(this, DocumentActivity::class.java)
            intent.putExtra("newDoc", true)
            startActivity(intent)
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

    private fun saveDocument() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        if (title.isBlank() && content.isBlank()) {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Replace with real API request to POST or PUT document
        Toast.makeText(this, "Saved '$title'", Toast.LENGTH_SHORT).show()
    }

    private fun confirmDelete() {
        if (documentId == null) {
            Toast.makeText(this, "This document hasn't been saved yet", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this document?")
            .setPositiveButton("Delete") { _, _ ->
                deleteDocument()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteDocument() {
        val id = documentId ?: return
        // TODO: Replace this with real DELETE request
        // DELETE /documents/{id}
        Toast.makeText(this, "Document deleted", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DocumentManagementActivity::class.java))
        finish()
    }
}