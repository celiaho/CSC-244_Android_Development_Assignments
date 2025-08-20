package edu.bhcc.cho.calculator

import java.math.BigDecimal
import java.math.RoundingMode
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// import androidx.activity.enableEdgeToEdge
// import edu.bhcc.cho.calculator.HistoryActivity

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
    private var pendingOperand2: String = ""
    private var currentOperation: String? = null
    private var equalsJustPressed: Boolean = false    // Flag to indicate if = button was just pressed
    private var operationJustPressed: Boolean = false // Flag to indicate if an operation button (+,-,*,/) was just pressed

    // For history storage
    private val calculationHistory = mutableListOf<HistoryItem>()
    private val historyLimit = 100 // Max number of history items to store

    // Activity Result launcher to receive selected result from HistoryActivity
    private val pickHistoryResult = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Pull the chosen result string and inject it as the new starting value
            val chosen = result.data?.getStringExtra("selected_result")
            if (!chosen.isNullOrBlank()) {
                display.text = chosen // Put it on screen
                operand1 = chosen.toDoubleOrNull() // Prep as first operand
                pendingOperand2 = ""               // Clear 2nd operand
                currentOperation = null            // Clear pending op
                equalsJustPressed = false
                operationJustPressed = false
            }
        }
    }

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
        btnMultiply.setOnClickListener { performOperation("x") }
        btnDivide.setOnClickListener { performOperation("/") }
        btnEquals.setOnClickListener { performEquals() }

        // Set OnClickListeners for utility buttons
        btnClear.setOnClickListener { clearDisplay() }
        btnToggleSign.setOnClickListener { toggleSign() }
        btnPercent.setOnClickListener { performPercent() }
        btnHistory.setOnClickListener {
            // Create an Intent to start the HistoryActivity
            val intent = android.content.Intent(this, HistoryActivity::class.java)
            // Last 100 calculations are copied to ArrayList for Intent
            val payload = ArrayList(calculationHistory.takeLast(historyLimit))
            intent.putExtra("history_list", payload) // Serializable
            pickHistoryResult.launch(intent) // Start for result
            Log.d("MainActivity", "History button clicked")
        }
    }

        // CATEGORIES OF BUTTONS
        // numbers
        // operation buttons
        // equals button
        // clear button
        // history button
        // toggle sign button
        // percent button
        // decimal button

        // what was pressed = newText
        // what's in the display = display.text.toString()
            //// displayText = display.text.toString()

        // If display =
            // wholeNum
            // wholeNum & operation
            // wholeNum & operation & wholeNum
            // wholeNum & operation & decimalNum
            // decimalNum
            // decimalNum & operation
            // decimalNum & operation & wholeNum
            // decimalNum & operation & decimalNum

        // ROOT LOGIC
            // When (we have operand1 + operation + operand2) + (another operation or =) >> THEN calculate operation
            // WHEN a 2nd nonNum is entered >> then calculate
            // OTHERWISE keep allowing input

        /*
        If it's the 2nd operator
        -if operatorJustPressed and operatorsPressed = 1 (or operation exists in current calculation and another operator just pressed

        if (newText == +-x or /) && (displayText contains +-x or /) {    // If a 2nd operator was entered
                calculate()
           } else { // A second operator was not entered
                // Display what was entered until an operator/= is pressed
                // Save that input
           }
         */

    // Function to append text to tv_display
    private fun appendToDisplay(newText: String) {
        Log.d("AppendToDisplay", "Text: $newText," +
                "Display: ${display.text}, equalsJustPressed: $equalsJustPressed"
        )

        if (newText == ".") {   // If user presses "."
            when {
                // If display string ends with an operation, append "0."
                display.text.toString().endsWith("+") || display.text.toString().endsWith("-") ||
                        display.text.toString().endsWith("x") || display.text.toString()
                    .endsWith("/") -> {
                    display.text = display.text.toString() + "0."
                }
                // If display is 0 or equals was just pressed, reset with "0."
                display.text == "0" || equalsJustPressed -> {
                    display.text = "0."
                    equalsJustPressed = false
                }
                // If an operation was just pressed, add "0." after the operation
                operationJustPressed -> {
                    display.text = display.text.toString() + "0."
                    operationJustPressed = false
                } else -> {   // If we're in the middle of entering a number
                    // Check if the current number already has a decimal
                    val displayText = display.text.toString()
                    val parts = displayText.split(Regex("[+\\-*/]"))
                    // Get the last part (current number being entered)
                    val currentNumber = parts.last()
                    // Add decimal if current number doesn't already have one
                    if (!currentNumber.contains(".")) {
                        display.text = "$displayText."
                    }
                }
            }
        } else {    // Handling for number inputs
            when {
                // If display is 0 or equals was just pressed, replace with new number
                display.text == "0" || equalsJustPressed -> {
                    display.text = newText
                    equalsJustPressed = false
                }
                // If an operation was just pressed, add new number after operation
                operationJustPressed -> {
                    display.text = display.text.toString() + newText
                    operationJustPressed = false
                    pendingOperand2 = newText // Start building the 2nd operand
                }

                else -> {    // If we're in the middle of entering a number
                    display.text = display.text.toString() + newText
                    // If we're entering the second operand, keep track of it
                    if (currentOperation != null && !operationJustPressed) {
                        pendingOperand2 += newText
                    }
                }
            }
        }
    }

    // Function to clear display & reset operands/operation
    private fun clearDisplay() {
        display.text = "0" // Set the text of the display TextView back to "0"
        operand1 = null // Clear the 1st operand
        pendingOperand2 = "" // Clear the pending 2nd operand
        currentOperation = null // Clear the current operation
        equalsJustPressed =
            false // Reset the equalsJustPressed flag to false (ready for a new calculation)
    }

    // Function to handle operation button clicks
    private fun performOperation(operation: String) {
        Log.d(
            "PerformOperation",
            "Operation: $operation, Display: ${display.text}, operand1: $operand1," +
                    "currentOperation: $currentOperation"
        )

        val displayString = display.text.toString() // Get the current display text

        // If we already have a pending operation, calculate it first
        if (operand1 != null && currentOperation != null && pendingOperand2.isNotEmpty()) {
            val operand2 = pendingOperand2.toDoubleOrNull() ?: 0.0
            val result = calculate(operand1!!, operand2, currentOperation!!)

            // Store the calculation in history
            val historyItem = HistoryItem(
                "${formatResult(operand1!!)} $currentOperation ${formatResult(operand2)} =",
                formatResult(result)
            )
            calculationHistory.add(historyItem)
            if (calculationHistory.size > historyLimit) {
                calculationHistory.removeAt(0)
            }

            operand1 = result
            display.text = formatResult(result) + operation
            pendingOperand2 = ""
        } else {
            // First operation in a calculation
            operand1 = displayString.toDoubleOrNull() ?: 0.0
            display.text = displayString + operation
            pendingOperand2 = ""
        }

        currentOperation = operation
        operationJustPressed = true
        equalsJustPressed = false
    }
    //        // Check if the display already has an operation in progress
//        val hasExistingOperation = displayString.contains("+") || displayString.contains("-") ||
//                displayString.contains("x") || displayString.contains("/")
//
//        if (hasExistingOperation && !operationJustPressed) {
//            performEquals()     // If there's an existing operation, evaluate it first
//            val resultString = display.text.toString()  // After evaluation, store display text as "resultString"
//            operand1 = resultString.toDoubleOrNull()    // Store result as operand1 for next operation
//            currentOperation = operation                // Set the new operation
//            display.text = resultString + operation
//        } else {    // No existing operation or operation just pressed
//            if (operand1 == null) { // If this is the first operand
//                operand1 = displayString.toDoubleOrNull()    // Store current display val as operand1
//                currentOperation = operation    // Set the new operation
//                display.text = displayString + operation
//            } else {    // Operation button pressed after another operation
//                currentOperation = operation    // Just update the operation
//                // Update display with new operation and replace last char if needed
//                if (operationJustPressed) {
//                    display.text = displayString.substring(0, displayString.length - 1) + operation
//                } else {
//                    display.text = displayString + operation
//                }
//            }
//        }

    /////////////////// Now = doesn't work. wtf.
//        if (operand1 != null && currentOperation != null && !operationJustPressed) {
//            // Perform the pending calculation
//            val operand2 = displayString.substringAfter(currentOperation!!).toDoubleOrNull()
//            if (operand2 != null) {
//                val result = calculate(operand1!!, operand2, currentOperation!!)
//                operand1 = result
//                currentOperation = operation
//                display.text = formatResult(result) + operation
//            } else {
//                // If no second operand, just update the operation
//                currentOperation = operation
//                val currentOperand1 = operand1 // Create a local immutable copy
//                if (currentOperand1 != null) {
//                    display.text = formatResult(currentOperand1) + operation
//                } else {
//                    // This case should ideally not happen if operand1 was checked for null earlier
//                    display.text = "0" + operation
//                }
//            }
//        } else {
//            // First operand or operation after equals/clear
//            operand1 = displayString.toDoubleOrNull()
//            currentOperation = operation
//            display.text = displayString + operation
//        }
//
//        ////
//        operationJustPressed = true
//        equalsJustPressed = false
//    }

    // Function to handle equals button click
    private fun performEquals() {
        if (currentOperation != null && operand1 != null) {
            // Only proceed if we have a second operand
            if (pendingOperand2.isNotEmpty()) {
                // Convert and calculate
                val operand2 = pendingOperand2.toDoubleOrNull() ?: 0.0
                val result = calculate(operand1!!, operand2, currentOperation!!)

                // Store in history
                val historyItem = HistoryItem(
                    "${formatResult(operand1!!)} $currentOperation ${formatResult(operand2)} =",
                    formatResult(result)
                )
                calculationHistory.add(historyItem)
                if (calculationHistory.size > historyLimit) {
                    calculationHistory.removeAt(0)
                }

                // Update display and reset for next calculation
                display.text = formatResult(result)
                operand1 = result
                pendingOperand2 = ""
                currentOperation = null
                equalsJustPressed = true
            }
            // If no second operand, do nothing and wait for user to enter one
        }
    }

//                if (currentOperation != null) {
//                val displayString = display.text.toString()
//
//                // Split the display text by the operation to get the operands
//                val operationIndex = when (currentOperation) {
//                    "+" -> displayString.lastIndexOf("+")
//                    "-" -> displayString.lastIndexOf("-")
//                    "x" -> displayString.lastIndexOf("x")
//                    "/" -> displayString.lastIndexOf("/")
//                    else -> -1
//                }
//
//                if (operationIndex > 0) {
//                    // Extract operands
//                    val leftOperand =
//                        displayString.substring(0, operationIndex).toDoubleOrNull() ?: 0.0
//                    val rightOperand =
//                        displayString.substring(operationIndex + 1).toDoubleOrNull() ?: 0.0
//
//                    // Perform calculation
//                    val result = calculate(leftOperand, rightOperand, currentOperation!!)
//
//                    // Format and display result
//                    display.text = formatResult(result)
//
//                    // Store the calculation in history
//                    val historyItem = HistoryItem(
//                        "$leftOperand $currentOperation $rightOperand =",
//                        formatResult(result)
//                    )
//                    calculationHistory.add(historyItem)
//                    if (calculationHistory.size > historyLimit) {
//                        calculationHistory.removeAt(0)
//                    }
//
//                    // Reset for next calculation
//                    operand1 = result
//                    currentOperation = null
//                    equalsJustPressed = true
////                }
//            }

    // Function to perform the calculation
    private fun calculate(num1: Double, num2: Double, operation: String): Double {
        // Convert to BigDecimal for precise calculations
        val bd1 = BigDecimal.valueOf(num1)
        val bd2 = BigDecimal.valueOf(num2)

        val result = when (operation) {
            "+" -> bd1.add(bd2)
            "-" -> bd1.subtract(bd2)
            "x" -> bd1.multiply(bd2)
            "/" -> if (bd2.compareTo(BigDecimal.ZERO) != 0) {
                bd1.divide(bd2, 10, RoundingMode.HALF_UP)
            } else {
                return Double.NaN // Handle division by zero
            }

            else -> return Double.NaN // Should not happen
        }

        return result.toDouble()
    }

    // Function to format the result
    private fun formatResult(result: Double): String {
        if (result.isNaN()) return "Error"

        val bd = BigDecimal.valueOf(result) // Use BigDecimal to properly format the result

        return if (bd.stripTrailingZeros().scale() <= 0) {  // If it's a whole number...
            bd.toBigInteger().toString()    // ...return the number as an int
        } else {
            bd.stripTrailingZeros()
                .toPlainString()     // Return decimal value with trailing zeros removed
        }
    }

    // Function to toggle the sign of the displayed number
    private fun toggleSign() {
        val currentValue = display.text.toString().toDoubleOrNull() // Get the current value
        if (currentValue != null) {
            display.text =
                formatResult(currentValue * -1) // Multiply currentValue by -1 and update display
        }
    }

    // Function to calculate the percentage of the displayed number
    private fun performPercent() {
        val currentValue = display.text.toString().toDoubleOrNull() // Get the current value
        if (currentValue != null) {
            display.text =
                formatResult(currentValue / 100.0) // Divide currentValue by 100 and update display
        }
    }
}

// Complete HistoryItem data class for storing history entries
//data class HistoryItem(val expression: String, val result: String)