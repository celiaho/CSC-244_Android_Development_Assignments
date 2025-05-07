package edu.bhcc.cho.noteserver.ui.document

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.bhcc.cho.noteserver.R

class DocumentSharePopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_share_popup)

        // TODO: setup RecyclerView logic
        Toast.makeText(this, "Sharing screen loaded", Toast.LENGTH_SHORT).show()
    }
}