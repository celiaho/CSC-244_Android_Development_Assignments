package edu.bhcc.cho.noteserver.ui.document

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bhcc.cho.noteserver.R
import edu.bhcc.cho.noteserver.data.model.Document

/**
 * Displays a list of Document items in a RecyclerView using item_document_list_tab.xml.
 */
class DocumentAdapter(
    private val context: Context,
    private var documents: List<Document>
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {
    /**
     * ViewHolder represents one item view in the RecyclerView.
     */
    inner class DocumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.text_document_title)
        val lastModifiedText: TextView = view.findViewById(R.id.text_document_last_modified_date)

        init {
            // When a document card is tapped, launch DocumentActivity with document ID
            view.setOnClickListener {
                val document = documents[adapterPosition]
                val intent = Intent(context, DocumentActivity::class.java).apply {
                    putExtra("DOCUMENT_ID", document.id)
                    putExtra("DOCUMENT_TITLE", document.title)
                    putExtra("DOCUMENT_CONTENT", document.content)
                }
                context.startActivity(intent)
            }
        }
    }

    /**
     * Inflates the layout for each document item in the RecyclerView.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of DocumentViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_list_tab, parent, false)
        return DocumentViewHolder(view)
    }

    /**
     * Binds document data to each item view in the RecyclerView to override dummy text for Document Title and Last Modified Date.
     *
     * @param holder The DocumentViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val doc = documents[position]
        holder.titleText.text = doc.title
        holder.lastModifiedText.text = "Last modified: ${doc.lastModifiedDate}"
    }

    /**
     * Returns the total number of documents to display.
     *
     * @return The size of the documents list.
     */
    override fun getItemCount(): Int = documents.size

    /**
     * Replaces the current list of documents with a new list and refreshes the RecyclerView.
     *
     * @param newDocuments The updated list of Document objects to display.
     */
    fun updateData(newDocuments: List<Document>) {
        documents = newDocuments
        notifyDataSetChanged()
    }
}