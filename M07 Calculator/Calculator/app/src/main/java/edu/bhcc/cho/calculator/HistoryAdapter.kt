package edu.bhcc.cho.calculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bhcc.cho.calculator.R    // Imports the R class to find the view IDs in item_history.xml

// Constructor. This class displays a list of HistoryItem objects in a RecyclerView.
class HistoryAdapter(
    private val historyList: List<HistoryItem>, // List of history items to display
    private val onItemClick: (String) -> Unit // Callback fn to execute when a history item is clicked; takes the result as a String
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() { // Inherits from RecyclerView.Adapter

    // This class holds references to the views for each history item
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView to display the calculation instruction
        val instructionTextView: TextView = itemView.findViewById(R.id.tv_history_instruction)
        // TextView to display the calculation result
        val resultTextView: TextView = itemView.findViewById(R.id.tv_history_result)
    }

    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        // Inflate the item_history.xml layout to create a new view
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        // Return a new ViewHolder that holds the inflated view
        return HistoryViewHolder(itemView)
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        // Get the HistoryItem at the current position in the list
        val currentItem = historyList[position]
        // Set the instruction text in the instructionTextView
        holder.instructionTextView.text = currentItem.instruction
        // Set the result text in the resultTextView
        holder.resultTextView.text = currentItem.result

        // Set OnClickListener on the entire item view (i.e. the entire row of a history item in RecyclerView)
        holder.itemView.setOnClickListener { // When the item is clicked, call the onItemClick callback fn
            onItemClick(currentItem.result) // & pass the result of the current item as an argument (so it can be displayed)
        }
    }

    // Return the total number of items in the historyList
    override fun getItemCount() = historyList.size
}