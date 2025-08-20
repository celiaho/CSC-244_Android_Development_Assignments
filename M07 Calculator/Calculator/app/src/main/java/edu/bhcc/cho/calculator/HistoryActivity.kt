package edu.bhcc.cho.calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Displays the last 100 (or fewer) calculations. Tapping a row returns the result
 * to MainActivity so it can be used as the next starting value.
 */
class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Find RecyclerView from activity_history.xml
        val recycler = findViewById<RecyclerView>(R.id.rv_history_list)

        // Get the Serializable list (ArrayList<HistoryItem>) from the Intent
        @Suppress("UNCHECKED_CAST")
        val historyList = intent.getSerializableExtra("history_list") as? ArrayList<HistoryItem>

        if (historyList.isNullOrEmpty()) {
            Toast.makeText(this, "No history yet", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            // Keep screen visible so  user can go back via system back
        } else {
            recycler.layoutManager = LinearLayoutManager(this)
            // Hook adapter with click callback: return the result string
            recycler.adapter = HistoryAdapter(historyList) { selectedResult ->
                // Prepare data to send back
                val data = Intent().putExtra("selected_result", selectedResult)
                setResult(RESULT_OK, data)
                finish() // Close and return to MainActivity
            }
        }
    }
}