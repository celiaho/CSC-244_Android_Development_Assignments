package edu.bhcc.cho.buttonclickingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


const val USER_TARGET_NUMBER = "edu.bhcc.cho.buttonclickingapp.USER_TARGET_NUMBER"

class Activity2 : AppCompatActivity() {
    private var currentTargetNumber = 5     // Default is 5 when app starts
    private lateinit var targetNumberDisplay: EditText  // To be initialized upon creation of Activity2 contentView
    // ??? WHY does line below give same output as above but trigger an error?
    // private val targetNumberDisplay: EditText by lazy { findViewById<TextView>(R.id.tv_target_numberDisplay) }
    private val setButton: Button by lazy { findViewById<Button>(R.id.btn_target_set) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_2)

        targetNumberDisplay = findViewById(R.id.tv_target_numberDisplay)    // Initialize lateinit

        setButton.setOnClickListener {
//            targetNumberDisplay.text = currentTargetNumber.toString() // the number entered
            currentTargetNumber = targetNumberDisplay.text.toString().toInt()
            goBack(it)
        }
    }

    private fun goBack(view: View) {    // Button that allows you to return to MainActivity
//        val message = targetNumberDisplay.text.toString().toInt()     // EXTRA_... = constant:MainActivity
//        val textView = findViewById<TextView>(R.id.tv_target_prompt) // prof's textView = tv_limit_prompt
//        textView.text = "You typed the following numbers \"$message\""
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(USER_TARGET_NUMBER, currentTargetNumber)   // Pass user's target number to MainActivity
        }

        startActivity(intent)
    }
}