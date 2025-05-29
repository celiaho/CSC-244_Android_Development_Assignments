package edu.bhcc.cho.noteserver.ui.document

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.Document
import edu.bhcc.cho.noteserver.data.network.DocumentApiService
import edu.bhcc.cho.noteserver.ui.auth.LoginActivity
import edu.bhcc.cho.noteserver.ui.settings.SettingsActivity

class DocumentManagementActivity : AppCompatActivity() {
    private val editDocLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data?.getBooleanExtra("REFRESH_NEEDED", false) == true) {
            loadDocuments()
        }
    }

    // Declare tab buttons
    private lateinit var tabMyFiles: Button
    private lateinit var tabSharedFiles: Button

    // Declare sections
    private lateinit var layoutMyFiles: LinearLayout
    private lateinit var layoutSharedFiles: LinearLayout

    // Declare RecyclerViews and empty states
    private lateinit var recyclerMyFiles: RecyclerView
    private lateinit var recyclerSharedFiles: RecyclerView
    private lateinit var emptyMyFiles: TextView
    private lateinit var emptySharedFiles: TextView

    // Declare adapters
    private lateinit var sharedFilesAdapter: DocumentAdapter

    // Declare DocumentAPIService
    private lateinit var apiService: DocumentApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_management)

        // Highlight Document Management icon in toolbar
        findViewById<ImageButton>(R.id.icon_open_folder)
            .setColorFilter(ContextCompat.getColor(this, R.color.orange))

        // *Handle toolbar clicks*
        // New Document Icon
        findViewById<ImageButton>(R.id.icon_new_doc).setOnClickListener {
            val intent = Intent(this, DocumentActivity::class.java)
            editDocLauncher.launch(intent)

        }
        // Document Management Icon - Already here
        findViewById<ImageButton>(R.id.icon_open_folder).setOnClickListener {
            Toast.makeText(this, "You are already in File Management.", Toast.LENGTH_SHORT).show()
        }
        // Settings Icon
        findViewById<ImageButton>(R.id.icon_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        // Logout Icon
        findViewById<ImageButton>(R.id.icon_logout).setOnClickListener {
            // TODO: Clear auth token if stored, return to Login
            startActivity(Intent(this, edu.bhcc.cho.noteserver.ui.auth.LoginActivity::class.java))
            finish()
        }

        apiService = DocumentApiService(this)

        // Initialize views
        tabMyFiles = findViewById(R.id.tab_my_files)
        tabSharedFiles = findViewById(R.id.tab_shared_files)
        layoutMyFiles = findViewById(R.id.layout_my_files)
        layoutSharedFiles = findViewById(R.id.layout_shared_files)
        recyclerMyFiles = findViewById(R.id.recycler_my_files)
        recyclerSharedFiles = findViewById(R.id.recycler_shared_files)
        emptyMyFiles = findViewById(R.id.empty_my_files)
        emptySharedFiles = findViewById(R.id.empty_shared_files)

        Log.d("---DOCUMENT_MANAGEMENT_PAGE_LOADED", "---DOCUMENT_MANAGEMENT_PAGE_LOADED")

        // Set up Recycler Layout Views/layout managers
        recyclerMyFiles.layoutManager = LinearLayoutManager(this)
        recyclerSharedFiles.layoutManager = LinearLayoutManager(this)

        // *Handle tab clicks*
        // My Files tab
        tabMyFiles.setOnClickListener {
            Log.d("---MY_FILES_TAB_CLICKED", "---MY_FILES_TAB_CLICKED")
            tabMyFiles.setBackgroundColor(getColor(R.color.blue))
            tabMyFiles.setTextColor(getColor(R.color.white))
            tabSharedFiles.setBackgroundColor(getColor(R.color.light_gray))
            tabSharedFiles.setTextColor(getColor(R.color.dark_gray))
            layoutMyFiles.visibility = View.VISIBLE
            layoutSharedFiles.visibility = View.GONE
        }
        // Shared Files tab
        tabSharedFiles.setOnClickListener {
            Log.d("---SHARED_FILES_TAB_CLICKED", "---SHARED_FILES_TAB_CLICKED")
            tabSharedFiles.setBackgroundColor(getColor(R.color.blue))
            tabSharedFiles.setTextColor(getColor(R.color.white))
            tabMyFiles.setBackgroundColor(getColor(R.color.light_gray))
            tabMyFiles.setTextColor(getColor(R.color.dark_gray))
            layoutSharedFiles.visibility = View.VISIBLE
            layoutMyFiles.visibility = View.GONE
        }

        // Fetch and display documents from the server
        loadDocuments()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val refresh = data?.getBooleanExtra("REFRESH_NEEDED", false) ?: false
            if (refresh) {
                Log.d("---REFRESHING_DOC_LIST", "---Detected document change, reloading list")
                loadDocuments()
            }
        }
    }

    /**
     * Loads documents from the backend API and updates the corresponding views.
     */
    private fun loadDocuments() {
        apiService.getDocuments(
            onSuccess = { documents ->
                val myFiles = documents // Show everything for now
                val sharedFiles = emptyList<Document>() // Placeholder for future sharing support
                // TEMPORARILY COMMENTED OUT FOR DEV
//                val myFiles = documents.filter { it.sharedWith.isEmpty() }
//                val sharedFiles = documents.filter { it.sharedWith.isNotEmpty() }

                // Log API response to verify
                Log.d("---DOCUMENTS_RECEIVED", "---Loaded ${documents.size} documents")
                documents.forEach { doc ->
                    Log.d("---DOC", "---ID=${doc.id}, title=${doc.title}, modified=${doc.lastModifiedDate}")
                }

                // Update My Files tab
                if (myFiles.isEmpty()) {
                    recyclerMyFiles.visibility = View.GONE
                    emptyMyFiles.visibility = View.VISIBLE
                } else {
                    // Set up adapter to connect the documents to the views inside each item in the list
                    recyclerMyFiles.adapter = DocumentAdapter(this, myFiles)
                    recyclerMyFiles.visibility = View.VISIBLE
                    emptyMyFiles.visibility = View.GONE
                }

                // Update Shared Files tab
                if (sharedFiles.isEmpty()) {
                    recyclerSharedFiles.visibility = View.GONE
                    emptySharedFiles.visibility = View.VISIBLE
                } else {
                    recyclerSharedFiles.adapter = DocumentAdapter(this, sharedFiles)
                    recyclerSharedFiles.visibility = View.VISIBLE
                    emptySharedFiles.visibility = View.GONE
                }
            },
            onError = {
                Log.d("---ERROR_LOADING_DOCUMENTS", "---Error loading documents: $it")
                Toast.makeText(this, "Error loading documents.", Toast.LENGTH_SHORT).show()
                emptyMyFiles.visibility = View.VISIBLE
                emptySharedFiles.visibility = View.VISIBLE
            }
        )
    }
}