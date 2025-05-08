package edu.bhcc.cho.noteserver.ui.document

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.ui.settings.SettingsActivity

class DocumentManagementActivity : AppCompatActivity() {
    private lateinit var tabMyFiles: Button
    private lateinit var tabSharedFiles: Button

    private lateinit var layoutMyFiles: LinearLayout
    private lateinit var layoutSharedFiles: LinearLayout

    private lateinit var recyclerMyFiles: RecyclerView
    private lateinit var recyclerSharedFiles: RecyclerView
    private lateinit var emptyMyFiles: TextView
    private lateinit var emptySharedFiles: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_management)

        // Bind views
        tabMyFiles = findViewById(R.id.tab_my_files)
        tabSharedFiles = findViewById(R.id.tab_shared_files)
        layoutMyFiles = findViewById(R.id.layout_my_files)
        layoutSharedFiles = findViewById(R.id.layout_shared_files)
        recyclerMyFiles = findViewById(R.id.recycler_my_files)
        recyclerSharedFiles = findViewById(R.id.recycler_shared_files)
        emptyMyFiles = findViewById(R.id.empty_my_files)
        emptySharedFiles = findViewById(R.id.empty_shared_files)

        // Setup RecyclerViews
        recyclerMyFiles.layoutManager = LinearLayoutManager(this)
        recyclerSharedFiles.layoutManager = LinearLayoutManager(this)

        // TODO: Replace with real adapters wired to data from server
        recyclerMyFiles.adapter = DummyFileAdapter(listOf("My Note 1", "My Note 2"))
        recyclerSharedFiles.adapter = DummyFileAdapter(listOf())  // Empty initially

        // Show/hide empty text
        emptyMyFiles.visibility = if ((recyclerMyFiles.adapter?.itemCount ?: 0) == 0) View.VISIBLE else View.GONE
        recyclerMyFiles.visibility = if ((recyclerMyFiles.adapter?.itemCount ?: 0) > 0) View.VISIBLE else View.GONE

        emptySharedFiles.visibility = if ((recyclerSharedFiles.adapter?.itemCount ?: 0) == 0) View.VISIBLE else View.GONE
        recyclerSharedFiles.visibility = if ((recyclerSharedFiles.adapter?.itemCount ?: 0) > 0) View.VISIBLE else View.GONE

        // Default to My Files
        showMyFilesTab()

        // Handle tab clicks
        tabMyFiles.setOnClickListener { showMyFilesTab() }
        tabSharedFiles.setOnClickListener { showSharedFilesTab() }

        // Toolbar icon buttons
        findViewById<ImageButton>(R.id.icon_new_doc).setOnClickListener {
            startActivity(Intent(this, DocumentActivity::class.java))
        }

        findViewById<ImageButton>(R.id.icon_open_folder).setOnClickListener {
            // Already here - maybe refresh or show toast?
            Toast.makeText(this, "You are already in File Management.", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.icon_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<ImageButton>(R.id.icon_logout).setOnClickListener {
            // TODO: Clear auth token if stored, return to Login
            startActivity(Intent(this, edu.bhcc.cho.noteserver.ui.auth.LoginActivity::class.java))
            finish()
        }

        // Hide icons not used on this screen
        findViewById<View>(R.id.icon_save)?.visibility = View.GONE
        findViewById<View>(R.id.icon_share)?.visibility = View.GONE
        findViewById<View>(R.id.icon_delete)?.visibility = View.GONE
    }

    private fun showMyFilesTab() {
        layoutMyFiles.visibility = View.VISIBLE
        layoutSharedFiles.visibility = View.GONE
        tabMyFiles.setBackgroundColor(resources.getColor(R.color.blue))
        tabMyFiles.setTextColor(resources.getColor(R.color.white))
        tabSharedFiles.setBackgroundColor(resources.getColor(R.color.light_gray))
        tabSharedFiles.setTextColor(resources.getColor(R.color.dark_gray))
    }

    private fun showSharedFilesTab() {
        layoutMyFiles.visibility = View.GONE
        layoutSharedFiles.visibility = View.VISIBLE
        tabSharedFiles.setBackgroundColor(resources.getColor(R.color.blue))
        tabSharedFiles.setTextColor(resources.getColor(R.color.white))
        tabMyFiles.setBackgroundColor(resources.getColor(R.color.light_gray))
        tabMyFiles.setTextColor(resources.getColor(R.color.dark_gray))
    }

    // Dummy adapter to simulate file display; replace with your own later
    class DummyFileAdapter(private val items: List<String>) : RecyclerView.Adapter<DummyFileAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val label: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = TextView(parent.context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(24, 16, 24, 16)
                textSize = 16f
                setTextColor(parent.context.getColor(R.color.dark_gray))
            }
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.label.text = items[position]
        }
    }
}