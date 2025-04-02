package edu.bhcc.cho.buttonclickingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


const val NEW_TARGET_NUMBER = "edu.bhcc.cho.buttonclickingapp.NEW_TARGET_NUMBER"

class MainActivity : AppCompatActivity() {
    private var currentDisplayNumber = 0
    private var targetNumber: Int = 5       // Default is 5 when app starts

    // RULE: When using multiple activities/orientations, check that ids are not null before using.
    private val numberDisplay: TextView by lazy { findViewById<TextView>(R.id.tv_click_numberDisplay) }
    private val minusButton: Button by lazy { findViewById<Button>(R.id.btn_click_minus) }
    private val plusButton: Button by lazy { findViewById<Button>(R.id.btn_click_plus) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        minusButton.setOnClickListener {
            currentDisplayNumber--
            updateDisplay()
            checkTarget()
        }

        plusButton.setOnClickListener {
            currentDisplayNumber++
            updateDisplay()
            checkTarget()
        }

        // Retrieve new target number from Activity2
        if (intent.hasExtra(USER_TARGET_NUMBER)) {
            targetNumber = intent.getIntExtra(USER_TARGET_NUMBER, 1)
        }
    }

    // Helper functions for onCreate function
    private fun updateDisplay() {
        numberDisplay.text = currentDisplayNumber.toString()
    }

    private fun checkTarget() {
        if (currentDisplayNumber == targetNumber) {    // If target is reached...
            toNextActivity()                    // ...start Activity2.
        }
    }

    // Ticket to Activity2
    private fun toNextActivity() {
        val message = numberDisplay.text.toString()     // Get the current number as a string
        // Intent is like a ticket to start other activities. Ticket goes from this (mainActivity) to Activity2
        // .apply lets you bring a carry-on (extra info e.g. user's input string) to the new activity
        val intent = Intent(this, Activity2::class.java).apply {
            // Sending the extra info as a map w/key-value pair (k=constant defined above, v=user's input)
            putExtra(NEW_TARGET_NUMBER, message)            // Pass new target number to Activity2
        }

        startActivity(intent)
    }
}