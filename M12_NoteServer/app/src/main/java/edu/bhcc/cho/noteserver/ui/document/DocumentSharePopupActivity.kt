package edu.bhcc.cho.noteserver.ui.document

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.network.DocumentApiService
import edu.bhcc.cho.noteserver.utils.SessionManager

// Resolve View and ViewGroup ambiguity
import android.view.View as AndroidView
import android.view.ViewGroup as AndroidViewGroup

class DocumentSharePopupActivity : AppCompatActivity() {

    private lateinit var userListView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var apiService: DocumentApiService
    private lateinit var sessionManager: SessionManager

    private var allUsers: MutableList<DocumentApiService.UserProfile> = mutableListOf()
    private var sharedUserIds: MutableSet<String> = mutableSetOf()

    private var documentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_share_popup)

        // Get document ID from intent extras
        documentId = intent.getStringExtra("DOCUMENT_ID") ?: ""

        // Init views and services
        userListView = findViewById(R.id.listViewUsers)
        apiService = DocumentApiService(this)
        sessionManager = SessionManager(this)

        // Start fetching users
        fetchSharableUsers()
    }

    private fun fetchSharableUsers() {
        val currentUserId = sessionManager.getUserId() ?: return

        apiService.getAllUsers(
            onSuccess = { users ->
                allUsers = users.filter { user -> user.id != currentUserId }.toMutableList()
                fetchCurrentlySharedUsers()
            },
            onError = { showToast(it) }
        )
    }

    private fun fetchCurrentlySharedUsers() {
        apiService.getSharedUsers(
            documentId,
            onSuccess = { ids ->
                sharedUserIds = ids.toMutableSet()
                renderUserList()
            },
            onError = { showToast(it) }
        )
    }

    private fun renderUserList() {
        adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            allUsers.map { "${it.firstName} ${it.lastName} - ${it.email}" }
        ) {
            override fun getView(position: Int, convertView: AndroidView?, parent: AndroidViewGroup): AndroidView {
                val view = super.getView(position, convertView, parent)
                val user = allUsers[position]
                view.setBackgroundColor(
                    if (sharedUserIds.contains(user.id)) {
                        ContextCompat.getColor(context, R.color.orange)
                    } else {
                        ContextCompat.getColor(context, android.R.color.transparent)
                    }
                )
                return view
            }
        }

        userListView.adapter = adapter

        userListView.setOnItemClickListener { _, _, position, _ ->
            val user = allUsers[position]
            if (sharedUserIds.contains(user.id)) {
                apiService.unshareDocumentWithUser(documentId, user.id,
                    onSuccess = {
                        sharedUserIds.remove(user.id)
                        renderUserList()
                    },
                    onError = { showToast(it) }
                )
            } else {
                apiService.shareDocumentWithUser(documentId, user.id,
                    onSuccess = {
                        sharedUserIds.add(user.id)
                        renderUserList()
                    },
                    onError = { showToast(it) }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}