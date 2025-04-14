package edu.bhcc.cho.calculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
// import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.bhcc.cho.calculator.HistoryActivity

class MainActivity : AppCompatActivity() {
    // Declare vars for all UI elements with "lateinit var" keyword (will initialize later in onCreate())
    private lateinit var display: TextView
    private lateinit var btnClear: Button
    private lateinit var btnToggleSign: Button
    private lateinit var btnPercent: Button
    private lateinit var btnDivide: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnMultiply: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btnSubtract: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btnAdd: Button
    private lateinit var btnHistory: Button
    private lateinit var btn0: Button
    private lateinit var btnDecimal: Button
    private lateinit var btnEquals: Button

    // Vars for operations and equals
    private var operand1: Double? = null
    private var currentOperation: String? = null
    private var equalsPressed: Boolean = false

    // For history storage
    private val calculationHistory = mutableListOf<HistoryItem>()
    private val historyLimit = 100 // Max number of history items to store

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize TextView: Find the "tv_display" TextView & assign to var "display"
        display = findViewById(R.id.tv_display) // This is the calculator's display field

        // Initialize the buttons
        btnClear = findViewById(R.id.btn_clear)
        btnToggleSign = findViewById(R.id.btn_toggleSign)
        btnPercent = findViewById(R.id.btn_percent)
        btnDivide = findViewById(R.id.btn_divide)
        btn7 = findViewById(R.id.btn_7)
        btn8 = findViewById(R.id.btn_8)
        btn9 = findViewById(R.id.btn_9)
        btnMultiply = findViewById(R.id.btn_multiply)
        btn4 = findViewById(R.id.btn_4)
        btn5 = findViewById(R.id.btn_5)
        btn6 = findViewById(R.id.btn_6)
        btnSubtract = findViewById(R.id.btn_subtract)
        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btnAdd = findViewById(R.id.btn_add)
        btnHistory = findViewById(R.id.btn_history)
        btn0 = findViewById(R.id.btn_0)
        btnDecimal = findViewById(R.id.btn_decimal)
        btnEquals = findViewById(R.id.btn_equals)

        // Set OnClickListeners for number & decimal buttons
        btn0.setOnClickListener { appendToDisplay("0") }
        btn1.setOnClickListener { appendToDisplay("1") }
        btn2.setOnClickListener { appendToDisplay("2") }
        btn3.setOnClickListener { appendToDisplay("3") }
        btn4.setOnClickListener { appendToDisplay("4") }
        btn5.setOnClickListener { appendToDisplay("5") }
        btn6.setOnClickListener { appendToDisplay("6") }
        btn7.setOnClickListener { appendToDisplay("7") }
        btn8.setOnClickListener { appendToDisplay("8") }
        btn9.setOnClickListener { appendToDisplay("9") }
        btnDecimal.setOnClickListener { appendToDisplay(".") }

        // Set OnClickListeners for operation & equals buttons
        btnAdd.setOnClickListener { performOperation("+") }
        btnSubtract.setOnClickListener { performOperation("-") }
        btnMultiply.setOnClickListener { performOperation("*") }
        btnDivide.setOnClickListener { performOperation("/") }
        btnEquals.setOnClickListener { performEquals() }

        // Set OnClickListeners for utility buttons
        btnClear.setOnClickListener { clearDisplay() }
        btnToggleSign.setOnClickListener { toggleSign() }
        btnPercent.setOnClickListener { performPercent() }
        btnHistory.setOnClickListener {
            // Create an Intent to start the HistoryActivity
            val intent = android.content.Intent(this, HistoryActivity::class.java)
            startActivity(intent) // Start the HistoryActivity
            Log.d("Calculator", "History button clicked") //// Delete later
        }
    }

    // Function to append text to tv_display
    private fun appendToDisplay(newText: String) {  // For when number/decimal buttons are clicked
        if (equalsPressed) { // If equals button was just pressed (a calculation was just performed)
            display.text = newText  // Replace display text (the result) with number just pressed for a fresh calculation
            equalsPressed = false   // Reset equalsPressed flag to false (ready for a new calculation)
        } else {
            if (newText == ".") { // If user enters a decimal point...)
                if (!display.text.contains(".")) { // If current text does not already contain a decimal point...
                    if (display.text == "0") { // ...and if current text displayed is "0"...
                        display.text = "0." // ...replace it with "0."
                    } else { // ...and if current display text is not "0"...
                        // Convert current text to a string & append new text entered, and assign it to display.text so it displays
                        display.text = display.text.toString() + newText
                    }
                }
            } else { // If user inputs a number
                if (display.text == "0") { // If current text is "0"...
                    display.text = newText  // ...replace current text with the new number input
                } else { // Otherwise, append the new number
                    display.text = display.text.toString() + newText
                }
            }
        }
    }

    // Function to clear display & reset operands/operation
    private fun clearDisplay() {
        display.text = "0" // Set the text of the display TextView back to "0"
        operand1 = null // Clear the 1st operand
        currentOperation = null // Clear the current operation
        equalsPressed = false // Reset the equalsPressed flag to false (ready for a new calculation)
    }

    // Function to handle operation button clicks
    private fun performOperation(operation: String) {
        if (operand1 == null) { // If this is the 1st operand...
            operand1 = display.text.toString()
                .toDoubleOrNull() // Convert current display text to a double and assign it to operand1
            currentOperation = operation // Set the currentOperation to the operation just pressed
            equalsPressed = false // Reset the equalsPressed flag
            display.text = "0" // Set the display text back to "0"
        } else { // If there's already a 1st operand...
            val operand2 = display.text.toString().toDoubleOrNull() // Convert current display text to a double & assign it to operand2
            if (operand2 != null) { // If there's a 2nd operand...
                val previousResult = calculate(operand1!!, operand2, currentOperation!!) // Perform the pending calculation & save result to previousResult
                display.text = formatResult(previousResult) // Format the result and display it
                operand1 = previousResult // The previousResult becomes the first operand for the new operation
                currentOperation = operation // Set the currentOperation to the operation just pressed
                equalsPressed = false // Reset equals pressed flag
            } else { // If no valid operand2...
                currentOperation = operation // ...update currentOperation to the newly clicked operation
            }
        }
    }

    // Function to handle equals button click
    private fun performEquals() {
        if (operand1 != null && currentOperation != null) { // If there's a 1st operand & an operation
                val operand2 = display.text.toString().toDoubleOrNull() // Save the current display value as operand2
            ////// ***^Make this a log statement to debug
                if (operand2 != null) { // If operand2 is a valid number...
                    val result = calculate(operand1!!, operand2, currentOperation!!) // Perform the calculation
                    val formattedResult = formatResult(result)  // Store the formatted result in formattedResult
                    val instruction = "${formatResult(operand1!!)} $currentOperation ${formatResult(operand2)} ="
                    display.text = formattedResult

                    // Add the calculation to the history
                    val historyItem = HistoryItem(instruction, formattedResult)
                    calculationHistory.add(historyItem)
                    // Keep only the last 'historyLimit' items
                    if (calculationHistory.size > historyLimit) {
                        calculationHistory.removeAt(0)
                    }

                    operand1 = null // Clear operand1 to prepare for a new operation
                    currentOperation = null // Clear currentOperation to prepare for a new operation
                    equalsPressed = true // Set the equalsPressed flag to true to handle the next number input
                }
            }
        }

    // Function to perform the calculation
    private fun calculate(num1: Double, num2: Double, operation: String): Double {
        return when (operation) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "x" -> num1 * num2
            "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN // Handle division by zero
            else -> Double.NaN // Should not happen
        }
    }

    // Function to format the result (remove trailing .0 if it's an integer)
    private fun formatResult(result: Double): String {
        return if (result == result.toInt().toDouble()) {   // If the result is a whole number
            result.toInt().toString()   // format it as an integer string
        } else {
            result.toString()   // format it as a regular string
        }
    }

    // Function to toggle the sign of the displayed number
    private fun toggleSign() {
        val currentValue = display.text.toString().toDoubleOrNull() // Get the current value
        if (currentValue != null) {
            display.text = formatResult(currentValue * -1) // Multiply currentValue by -1 and update display
        }
    }

    // Function to calculate the percentage of the displayed number
    private fun performPercent() {
        val currentValue = display.text.toString().toDoubleOrNull() // Get the current value
        if (currentValue != null) {
            display.text = formatResult(currentValue / 100.0) // Divide currentValue by 100 and update display
        }
    }
}
