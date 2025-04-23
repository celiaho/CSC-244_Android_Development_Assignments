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
    private var equalsJustPressed: Boolean = false  // Flag to indicate if = button was just pressed
    private var operationJustPressed: Boolean = false // Flag to indicate if an operation button (+,-,*,/) was just pressed

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
        Log.d("AppendToDisplay", "Text: $newText, Display: ${display.text}, equalsJustPressed: $equalsJustPressed, operationJustPressed: $operationJustPressed")
        // If display is "0" OR "=" was just pressed OR (an operation was just pressed AND the display text is ONLY the operator)...
        if (display.text == "0" || equalsJustPressed || (operationJustPressed && (display.text == "+" || display.text == "-" || display.text == "x" || display.text == "/"))) {
            display.text = if (newText == ".") "0." else newText    // Prepend "0" if the first input is "."
            equalsJustPressed = false
            operationJustPressed = false
        } else {
            if (newText == ".") {   // If user presses "."
                if (!display.text.contains(".")) {  // and current display text doesn't already contain "."
                    display.text = display.text.toString() + newText    // append "." to current display text
                }
            } else {    // If user presses something other than "."
                display.text = display.text.toString() + newText    // append new input to current display text
            }
        }
    }
//    private fun appendToDisplay(newText: String) {  // For when number/decimal buttons are clicked
//        // If display is 0 OR = was just pressed OR an operation button was just pressed & the display text is one of the operators...
//        if (display.text == "0" || equalsJustPressed || (operationJustPressed && (display.text == "+" || display.text == "-" || display.text == "x" || display.text == "/"))) {
//            display.text = if (newText == ".") "0." else newText // Prepend "0" if the first input is "."
//            equalsJustPressed = false
//            operationJustPressed = false
//        } else {
//            if (newText == ".") {   // If user presses "."
//                if (!display.text.contains(".")) {  // and current display text doesn't already contain "."
//                    display.text = display.text.toString() + newText    // append "." to current display text
//                }
//            } else {    // If user presses something other than "."
//                display.text = display.text.toString() + newText    // append new input to current display text
//            }
//        }
//    }

    // Function to clear display & reset operands/operation
    private fun clearDisplay() {
        display.text = "0" // Set the text of the display TextView back to "0"
        operand1 = null // Clear the 1st operand
        currentOperation = null // Clear the current operation
        equalsJustPressed = false // Reset the equalsJustPressed flag to false (ready for a new calculation)
    }

    // Function to handle operation button clicks
    private fun performOperation(operation: String) {
        Log.d("PerformOperation", "Operation: $operation, Display: ${display.text}, operand1: $operand1, currentOperation: $currentOperation, operationJustPressed: $operationJustPressed")
        val currentDisplayValue = display.text.toString() // Get the current display text
        val currentValue = currentDisplayValue.toDoubleOrNull() // Convert to Double if possible

        if (operand1 == null) { // If this is the first operand
            operand1 = currentValue // Store the current display value
            currentOperation = operation // Set the operation
            operationJustPressed = true // Flag that an operation was pressed
            display.text = currentDisplayValue + operation // Append the operator to the display
        } else { // If there's already a first operand
            if (!operationJustPressed) { // If a second operand has been entered
                if (currentValue != null) { // If the second operand is valid
                    val result = calculate(operand1!!, currentValue, currentOperation!!) // Perform calculation
                    operand1 = result // Store the result
                    currentOperation = operation // Set the new operation
                    display.text = formatResult(result) + operation // Display result + new operator
                    operationJustPressed = true // Flag that an operation was pressed
                } else { // If no valid second operand
                    currentOperation = operation // Update the operation
                    display.text = formatResult(operand1 ?: 0.0) + operation // Display first operand + new operator
                    operationJustPressed = true // Flag that an operation was pressed
                }
            } else { // If an operation was pressed immediately after another
                currentOperation = operation // Update the operation
                display.text = formatResult(operand1 ?: 0.0) + operation // Display first operand + new operator
                operationJustPressed = true // Flag that an operation was pressed
            }
        }
    }

//        if (operand1 == null) { // If this is the 1st operand...
//            // Convert the current display string to a double and store as the first operand
//            operand1 = currentDisplay.toDoubleOrNull()
//            currentOperation = operation // Set the currentOperation to the operation just pressed
//            operationJustPressed = true // Indicate that an operation button was just pressed
//            display.text = currentDisplay + operation // Append the operation to the currentDisplay string
//        } else { // If there's already a first operand
//            if (!operationJustPressed) { // If a second operand has been entered
//                if (currentDisplayValue != null) { // If the second operand is a valid number
//                    val result = calculate(operand1!!, currentDisplayValue, currentOperation!!) // Perform the calculation
//                    operand1 = result // Store the result as the new first operand
//                    currentOperation = operation // Set the new operation
//                    display.text = formatResult(result) + operation // Display the result followed by the new operation
//                    operationJustPressed = true // Indicate that an operation button was pressed
//                } else { // If no valid second operand
//                    currentOperation = operation // Just update the current operation
//                    // Display the first operand followed by the new operation
//                    display.text = formatResult(operand1 ?: 0.0) + operation
//                    operationJustPressed = true // Indicate that an operation button was pressed
//                }
//            } else { // If an operation button was pressed immediately after another operation
//                currentOperation = operation // Update the current operation
//                // Display the first operand followed by the new operation
//                display.text = formatResult(operand1 ?: 0.0) + operation
//                operationJustPressed = true // Indicate that an operation button was pressed
//            }
//        }
//    }

    // Function to handle equals button click
    private fun performEquals() {
        if (operand1 != null && currentOperation != null) { // If there's a 1st operand & an operation
            val displayText = display.text.toString() // Get the current display text
            // Extract the second operand by removing the first operand and the operation
            val operand2String = displayText.substringAfter(currentOperation!!)
            val operand2 = operand2String.toDoubleOrNull() // Convert the extracted part to a double

            if (operand2 != null) { // If operand2 is a valid number...
                val result = calculate(operand1!!, operand2, currentOperation!!) // Perform the calculation
                display.text = formatResult(result) // Display the result
                operand1 = null // Reset operand1
                currentOperation = null // Reset current operation
                equalsJustPressed = true    // Indicate = was just pressed
            }
        }
    }

//                    val formattedResult = formatResult(result)  // Store the formatted result in formattedResult
//                    val instruction = "${formatResult(operand1!!)} $currentOperation ${formatResult(operand2)} ="
//                    display.text = formattedResult
//
//                    // Add the calculation to the history
//                    val historyItem = HistoryItem(instruction, formattedResult)
//                    calculationHistory.add(historyItem)
//                    // Keep only the last 'historyLimit' items
//                    if (calculationHistory.size > historyLimit) {
//                        calculationHistory.removeAt(0)
//                    }
//
//                    operand1 = null // Clear operand1 to prepare for a new operation
//                    currentOperation = null // Clear currentOperation to prepare for a new operation
//                    equalsJustPressed = true // Set the equalsJustPressed flag to true to handle the next number input

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
