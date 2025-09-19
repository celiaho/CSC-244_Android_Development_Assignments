package edu.bhcc.cho.calculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import com.google.gson.Gson // For JSON serialization
import com.google.gson.reflect.TypeToken // To restore typed ArrayList
import androidx.core.content.edit


/**
 * Main calculator activity (portrait + landscape).
 * - Portrait: digits 0–9, decimal, CLEAR, HISTORY, +/-, %, + - × ÷, =
 * - Landscape: add √x, n√x, x², xⁿ, log10, ln
 * - History screen: shows up to 100 entries; tapping a past RESULT returns to Main and seeds the display
 *
 * Notes:
 * - Basic ops use BigDecimal for steadier decimals; scientific ops use Double.
 * - History is serialized (ArrayList<HistoryItem>) via Intent extras.
 * - Rotation persistence implemented via onSaveInstanceState/onCreate restore.
 */
class MainActivity : AppCompatActivity() {
    // ==== UI refs ====
    // Declare vars for all UI elements with "lateinit" keyword (will initialize later in onCreate())
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

    // ==== State (tabulation approach) ====
    // Vars for operations and equals
    private var operand1: Double? = null
    private var pendingOperand2: String = ""
    private var currentOperation: String? = null
    private var equalsJustPressed = false    // Flag to indicate if = button was just pressed
    private var operationJustPressed =
        false // Flag to indicate if an operation button was just pressed

    // ==== History Storage ====
    private val calculationHistory = mutableListOf<HistoryItem>()
    private val historyLimit = 100 // Max number of history items to store

    // ==== History Persistence ====
    private val prefs by lazy { getSharedPreferences("calculator_prefs", MODE_PRIVATE) }
    private val gson = Gson()

    // ==== State keys for rotation persistence ====
    private companion object {
        const val KEY_DISPLAY = "state_display"
        const val KEY_OP1 = "state_operand1"
        const val KEY_OP2_STR = "state_pendingOperand2"
        const val KEY_CUR_OP = "state_currentOperation"
        const val KEY_EQ_JUST = "state_equalsJustPressed"
        const val KEY_OP_JUST = "state_operationJustPressed"
        const val KEY_HISTORY = "state_history"
    }

    // ==== ActivityResult: receive chosen past result from HistoryActivity ====
    private val pickHistoryResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Pull the chosen result string and inject it as the new starting value
            val selectedResult = result.data?.getStringExtra("selected_result")
            if (!selectedResult.isNullOrBlank()) {
                display.text =
                    selectedResult              // Put selected result from History into display
                operand1 = selectedResult.toDoubleOrNull() // Make it the first operand
                pendingOperand2 = ""               // Clear 2nd operand
                currentOperation = null            // Clear pending op
                equalsJustPressed = false
                operationJustPressed = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Keep content out of system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ---- Bind views ----
        // Initialize TextView: Find the "tv_display" TextView & assign to var "display"
        display = findViewById(R.id.tv_display) // This is the calculator's display field

        // Initialize buttons
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

        // ---- Load history from SharedPreferences if available ----
        prefs.getString("history_list", null)?.let { json ->
            val type = object : TypeToken<ArrayList<HistoryItem>>() {}.type
            val restored: ArrayList<HistoryItem> = gson.fromJson(json, type)
            calculationHistory.clear()
            calculationHistory.addAll(restored)
        }

        // ---- Restore state if present ----
        savedInstanceState?.let { b ->
            display.text = b.getString(KEY_DISPLAY, "0")
            operand1 = b.getString(KEY_OP1)?.toDoubleOrNull()
            pendingOperand2 = b.getString(KEY_OP2_STR, "")
            currentOperation = b.getString(KEY_CUR_OP)
            equalsJustPressed = b.getBoolean(KEY_EQ_JUST, false)
            operationJustPressed = b.getBoolean(KEY_OP_JUST, false)

            @Suppress("UNCHECKED_CAST")
            val restored = b.getSerializable(KEY_HISTORY) as? ArrayList<HistoryItem>
            calculationHistory.clear()
            if (restored != null) calculationHistory.addAll(restored)
        }

        // ---- Set listeners for digits & decimal buttons ----
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

        // ---- Set listeners for basic operations & equals buttons ----
        btnAdd.setOnClickListener { performOperation("+") }
        btnSubtract.setOnClickListener { performOperation("-") }
        btnMultiply.setOnClickListener { performOperation("x") }
        btnDivide.setOnClickListener { performOperation("/") }
        btnEquals.setOnClickListener { performEquals() }

        // ---- Set listeners for utility buttons ----
        btnClear.setOnClickListener { clearDisplay() }
        btnToggleSign.setOnClickListener { toggleSign() }
        btnPercent.setOnClickListener { performPercent() }

        // ---- Set listener for History button ----
        btnHistory.setOnClickListener { // Start for result & pass Serializable history
            // Create an Intent to start the HistoryActivity
            val intent = android.content.Intent(this, HistoryActivity::class.java)
            // Last 100 calculations are copied to ArrayList for Intent
            val payload = ArrayList(calculationHistory.takeLast(historyLimit)) // Serializable
            intent.putExtra("history_list", payload)
            pickHistoryResult.launch(intent) // Start for result
            Log.d("MainActivity", "History button clicked")
        }

        // ---- Set listeners for scientific operations buttons if they exist (landscape view only) ----
        findViewById<Button?>(R.id.btn_sqrt)?.setOnClickListener { performUnaryScientific(UnaryOp.SQRT) }
        findViewById<Button?>(R.id.btn_square)?.setOnClickListener { performUnaryScientific(UnaryOp.SQUARE) }
        findViewById<Button?>(R.id.btn_log)?.setOnClickListener { performUnaryScientific(UnaryOp.LOG10) }
        findViewById<Button?>(R.id.btn_ln)?.setOnClickListener { performUnaryScientific(UnaryOp.LN) }
        findViewById<Button?>(R.id.btn_power)?.setOnClickListener { performOperation("^") }
        findViewById<Button?>(R.id.btn_nth_root)?.setOnClickListener { performOperation("n√") }
    }

    // Persist state across rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_DISPLAY, display.text.toString())
        outState.putString(KEY_OP1, operand1?.toString())
        outState.putString(KEY_OP2_STR, pendingOperand2)
        outState.putString(KEY_CUR_OP, currentOperation)
        outState.putBoolean(KEY_EQ_JUST, equalsJustPressed)
        outState.putBoolean(KEY_OP_JUST, operationJustPressed)
        outState.putSerializable(KEY_HISTORY, ArrayList(calculationHistory))
    }

    /** Save calculation history permanently whenever the app goes to background. */
    override fun onPause() {
        super.onPause()
        val json = gson.toJson(ArrayList(calculationHistory)) // Convert to JSON string
        prefs.edit { putString("history_list", json) } // Store in SharedPreferences
    }

    // =========================================================================================
    // Input helpers
    // =========================================================================================

    /**
     * Append a number or decimal to the TextView display while respecting typing state (tabulation).
     */
    private fun appendToDisplay(newText: String) {
        Log.d(
            "AppendToDisplay",
            "Text: $newText, Display: ${display.text}, equalsJustPressed: $equalsJustPressed"
        )

        if (display.text == "Error") { // If user presses a digit after Error is displayed
            display.text = newText
            equalsJustPressed = false
            operationJustPressed = false
            operand1 = null
            pendingOperand2 = ""
            currentOperation = null
            return
        }

        if (newText == ".") {   // If user presses "."
            when {
                // If display string ends with an operation, append "0."
                display.text.toString().endsWith("+") ||
                        display.text.toString().endsWith("-") ||
                        display.text.toString().endsWith("x") ||
                        display.text.toString().endsWith("/") ||
                        display.text.toString().endsWith("^") ||
                        display.text.toString().endsWith("n√") -> {
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
                }

                else -> {   // If we're in the middle of entering a number
                    // Check if the current number already has a decimal
                    val displayText = display.text.toString()
                    val parts = displayText.split(Regex("[+\\-x/]|\\^|n√"))
                    // Get the last part (current number being entered)
                    val currentNumber = parts.last()
                    // Add decimal if current number doesn't already have one
                    if (!currentNumber.contains(".")) {
                        display.text = "$displayText."
                    }
                }
            }
            return
        }

        // If user started with a leading "-" then types a digit, build "-1", "-2", etc.
        if (display.text.toString() == "-" && newText != ".") {
            display.text = "-$newText"
            operand1 = null
            pendingOperand2 = ""
            return
        }

        // Digits
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

    /** Resets display and clears pending operation/operands. */
    private fun clearDisplay() {
        display.text = "0" // Set the text of the display TextView back to "0"
        operand1 = null // Clear the 1st operand
        pendingOperand2 = "" // Clear the pending 2nd operand
        currentOperation = null // Clear the current operation
        equalsJustPressed = false // Reset flag to false (ready for a new calculation)
        operationJustPressed = false // Reset flag to false
    }

    // =========================================================================================
    // Core tabulation: operations & equals
    // =========================================================================================

    /**
     * Handle an operation button click.
     * If an operation is already pending and operand2 has been entered, compute it first,
     * push to history, then continue chaining with the new operation.
     */
    private fun performOperation(operation: String) {
        Log.d(
            "PerformOperation", "Operation: $operation, " + "Display: ${display.text}, " +
                    "operand1: $operand1," + "currentOperation: $currentOperation"
        )

        val displayString = display.text.toString() // Get the current display text

        // Leading minus starts a negative number, not a subtraction from 0
        if (operation == "-" &&
            operand1 == null && currentOperation == null &&
            displayString == "0"
        ) {
            val noPendingOp = currentOperation == null
            val op1UnsetOrZero =
                (operand1 == null || displayString == "0" || displayString == "Error")
            // Only treat as "start negative" when there's no pending op and screen is fresh
            if (noPendingOp && op1UnsetOrZero) {
                display.text = "-"  // User is starting to type a negative number
                equalsJustPressed = false
                operationJustPressed = false
                return // DO NOT set currentOperation yet
            }
        }

        // If we already have a pending operation, calculate it first
        if (operand1 != null && currentOperation != null && pendingOperand2.isNotEmpty()) {
            val operand2 = pendingOperand2.toDoubleOrNull() ?: 0.0
            val result = calculate(operand1!!, operand2, currentOperation!!)

            // Store the completed calculation in history (keep the last 100 calculations)
            val historyExpression = if (currentOperation == "n√") {
                "${formatResult(operand1!!)} n√(${formatResult(operand2)}) ="
            } else {
                "${formatResult(operand1!!)} $currentOperation ${formatResult(operand2)} ="
            }

            val historyItem = HistoryItem(
                historyExpression,                                            //// CHANGED to use historyExpression
                formatResult(result)
            )
            calculationHistory.add(historyItem)
            if (calculationHistory.size > historyLimit) {
                calculationHistory.removeAt(0)
            }

            operand1 = result
            display.text = formatResult(result) + operation // Continue chaining
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

    /**
     * Compute the pending operation when '=' is pressed.
     * Push the completed instruction to history and prepare for the next chain.
     */
    private fun performEquals() {
        // Only proceed if we have a second operand
        if (currentOperation != null && operand1 != null && pendingOperand2.isNotEmpty()) {
            // Convert and calculate
            val operand2 = pendingOperand2.toDoubleOrNull() ?: 0.0
            val result = calculate(operand1!!, operand2, currentOperation!!)

            // Store completed calculation in history
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
            operationJustPressed = false

            // If no second operand, do nothing and wait for user to enter one
        }
    }

    // =========================================================================================
    // Math core
    // =========================================================================================

    /**
     * Performs a binary calculation between two numbers based on the given operation.
     *
     * For arithmetic operations (+, -, x, /), calculations are done with BigDecimal
     * for precision. For exponentiation and roots, Double.pow() is used.
     *
     * @param num1 The first operand (for most operations, this is the left-hand side).
     *             For "n√", this represents the root degree (e.g., 3 in 3 n√ 9).
     * @param num2 The second operand (for most operations, this is the right-hand side).
     *             For "n√", this represents the value whose root is being taken (e.g., 9 in 3 n√ 9).
     * @param operation The operation symbol: "+", "-", "x", "/", "^", or "n√".
     *
     * @return The result of the calculation as a Double, or Double.NaN if invalid
     *         (e.g., division by zero, zero root degree, negative base with fractional exponent).
     */
    private fun calculate(num1: Double, num2: Double, operation: String): Double {
        // Convert to BigDecimal for precise calculations; scientific ops use Double
        val bd1 = BigDecimal.valueOf(num1)
        val bd2 = BigDecimal.valueOf(num2)

        val result = when (operation) {
            "+" -> bd1.add(bd2).toDouble()
            "-" -> bd1.subtract(bd2).toDouble()
            "x" -> bd1.multiply(bd2).toDouble()
            "/" -> if (bd2.compareTo(BigDecimal.ZERO) != 0) {
                bd1.divide(bd2, 10, RoundingMode.HALF_UP).toDouble()
            } else {
                return Double.NaN // Handle division by zero
            }

            "^" -> num1.pow(num2) // exponentiation x^n
            "n√" -> {             // nth root (e.g., 3 n√ 9 = cube root of 9)
                if (num1 == 0.0) return Double.NaN
                // For negative base with fractional exponent, Double.pow -> NaN
                else num2.pow(1.0 / num1)
            }

            else -> return Double.NaN // Should not happen
        }

        return result.toDouble()
    }

    /**
     * Format numeric results nicely:
     *  - Whole numbers without decimal point
     *  - Decimals without trailing zeros
     *  - "Error" if the value is NaN
     */
    private fun formatResult(result: Double): String {
        if (result.isNaN()) return "Error"

        val bd = BigDecimal.valueOf(result) // Use BigDecimal to properly format the result
        return if (bd.stripTrailingZeros().scale() <= 0) {  // If it's a whole number...
            bd.toBigInteger().toString()    // ...return the number as an int
        } else {
            bd.stripTrailingZeros().toPlainString() // Return decimal val without trailing zeros
        }
    }

    // =========================================================================================
    // Utility ops: +/- and %
    // =========================================================================================

    /** Toggle the sign of the current display value. */
    private fun toggleSign() {
        val currentValue = display.text.toString().toDoubleOrNull() // Get the current value
        if (currentValue != null) {
            display.text =
                formatResult(currentValue * -1) // Multiply currentValue by -1 & update display
        }

        // If we were typing operand2, keep its string in sync with the new display segment
        if (currentOperation != null && !operationJustPressed && pendingOperand2.isNotEmpty()) {
            val afterOp = display.text
                .toString()
                .split(Regex("[+\\-x/]|\\^|n√"))
                .last() // the active numeric segment on the right
            pendingOperand2 = afterOp
        }
    }

    /**
     * Applies "percent" as a unary operation to the active numeric segment:
     * - If no binary operator is pending → divide the whole display by 100.
     * - If a binary operator is pending → divide the right-hand (operand2) segment by 100
     *   and replace only that segment on screen.
     *
     * Also records the operation in History in the form "%(segment) = result".
     */
    private fun performPercent() {
        val current = display.text.toString()

//        val currentValue = display.text.toString().toDoubleOrNull() // Get the current value
//        if (currentValue != null) {
//            display.text = formatResult(currentValue / 100.0) // Divide currentValue by 100 & update display
//        }
//
//        if (currentOperation == null) {
//            operand1 = display.text.toString().toDoubleOrNull()
//        } else if (!operationJustPressed) {
//            val afterOp = display.text
//                .toString()
//                .split(Regex("[+\\-x/]|\\^|n√"))
//                .last()
//            pendingOperand2 = afterOp
//        }

        // Find last operator index (ignore a leading '-')
        val lastMinus = current.drop(1).lastIndexOf('-')
        val minusIndex = if (lastMinus >= 0) lastMinus + 1 else -1
        val lastOpIndex = listOf(
            current.lastIndexOf('+'),
            minusIndex,
            current.lastIndexOf('x'),
            current.lastIndexOf('/'),
            current.lastIndexOf('^'),
            current.lastIndexOf("n√")
        ).maxOrNull() ?: -1

        // Active numeric segment is to the right of the last operator (or entire display)
        val segment = if (lastOpIndex >= 0) current.substring(lastOpIndex + 1) else current
        val x = segment.toDoubleOrNull() ?: return

        // Compute percent and format
        val raw = x / 100.0
        val formatted = formatResult(raw)

        // Record in history as a unary op
        calculationHistory.add(HistoryItem("%($segment) =", formatted))
        if (calculationHistory.size > historyLimit) calculationHistory.removeAt(0)

        // Update the display & state
        if (lastOpIndex >= 0 && currentOperation != null) {
            // We were entering operand2 → replace just that segment
            display.text = current.substring(0, lastOpIndex + 1) + formatted
            pendingOperand2 = formatted
            equalsJustPressed = false
            operationJustPressed = false
        } else {
            // No pending op → transform the whole value
            display.text = formatted
            operand1 = formatted.toDoubleOrNull()
            pendingOperand2 = ""
            currentOperation = null
            equalsJustPressed = false
            operationJustPressed = false
        }
    }

    // =========================================================================================
    // Scientific (unary) ops
    // =========================================================================================

    /** Supported unary scientific operations. */
    private enum class UnaryOp { SQRT, SQUARE, LOG10, LN }

    /** Helper: Returns the active numeric segment to operate on (handles leading negatives). */
    private fun currentNumericSegment(): String {
        val displayString = display.text.toString() // Get the current display text

        // If no pending binary op, operate on the whole display; fix "0-1" to "-1"
        if (currentOperation == null) {
            if (displayString.startsWith("0-")
                && !displayString.contains('+') && !displayString.contains('x') && !displayString.contains(
                    '/'
                )
                && !displayString.contains('^') && !displayString.contains("n√")
            ) {
                return "-" + displayString.substring(2)   // "0-1" -> "-1"
            }
            return displayString
        }

        // There is a pending op: operate on the right of the last operator (ignore a leading '-')
        val lastPlus = displayString.lastIndexOf('+')
        val lastMinus = displayString.drop(1).lastIndexOf('-')
            .let { if (it >= 0) it + 1 else -1 } // ignore leading '-'
        val lastMul = displayString.lastIndexOf('x')
        val lastDiv = displayString.lastIndexOf('/')
        val lastPow = displayString.lastIndexOf('^')
        val lastNrt = displayString.lastIndexOf("n√")
        val idx = listOf(lastPlus, lastMinus, lastMul, lastDiv, lastPow, lastNrt).max()
        return if (idx >= 0) displayString.substring(idx + 1) else displayString
    }

    /**
     * Applies a unary scientific operation (√, x², log10, ln) to the active numeric segment.
     *
     * Logic:
     * - If no binary operator is pending, operate on the entire display string.
     * - If a binary operator is pending, isolate the numeric segment to the right of it
     *   (e.g., in "30+5", applying √ affects "5").
     * - Prevents invalid math (negative √, log ≤ 0, ln ≤ 0) by showing "Error" and clearing state.
     * - Records the operation in History with a readable instruction like "x²(30) = 900".
     *
     * @param op The unary operation to apply (SQRT, SQUARE, LOG10, LN).
     */
    private fun performUnaryScientific(op: UnaryOp) {
        val current = display.text.toString()

        // Find last operator index to isolate the active right-side segment
        // Ignore a leading '-' (for negative numbers).
        // Only add +1 to the dropped-minus index if one was actually found.
        val lastMinus = current.drop(1).lastIndexOf('-')
        val minusIndex = if (lastMinus >= 0) lastMinus + 1 else -1

        val lastOpIndex = listOf(
            current.lastIndexOf('+'),
            minusIndex,
            current.lastIndexOf('x'),
            current.lastIndexOf('/'),
            current.lastIndexOf('^'),
            current.lastIndexOf("n√")
        ).maxOrNull() ?: -1

        // Isolate the active segment: either right of the last operator or the whole display
        val segment = if (lastOpIndex >= 0) current.substring(lastOpIndex + 1) else current
        val x = segment.toDoubleOrNull() ?: return

        // Perform the operation
        val raw = when (op) {
            UnaryOp.SQRT -> if (x < 0.0) Double.NaN else kotlin.math.sqrt(x)
            UnaryOp.SQUARE -> x.pow(2.0)
            UnaryOp.LOG10 -> if (x <= 0.0) Double.NaN else log10(x)
            UnaryOp.LN -> if (x <= 0.0) Double.NaN else ln(x)
        }

        // Handle invalid results (NaN)
        if (raw.isNaN()) {
            // Surface error and reset pending state
            display.text = "Error"
            operand1 = null
            pendingOperand2 = ""
            currentOperation = null
            equalsJustPressed = false
            operationJustPressed = false
            return
        }

        val formatted = formatResult(raw)

        // Record unary op in History
        val opLabel = when (op) {
            UnaryOp.SQRT -> "√"
            UnaryOp.SQUARE -> "x²"
            UnaryOp.LOG10 -> "log"
            UnaryOp.LN -> "ln"
        }
        calculationHistory.add(HistoryItem("$opLabel($segment) =", formatted))
        if (calculationHistory.size > historyLimit) calculationHistory.removeAt(0)

        // Update display and state
        if (lastOpIndex >= 0 && currentOperation != null) {
            // We were entering operand2; replace that segment and keep building
            display.text = current.substring(0, lastOpIndex + 1) + formatted
            pendingOperand2 = formatted
        } else {
            // No pending op; transform the primary value
            display.text = formatted
            operand1 = formatted.toDoubleOrNull()
            equalsJustPressed = false
            operationJustPressed = false
        }
    }
}