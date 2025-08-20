package edu.bhcc.cho.calculator

import java.io.Serializable

data class HistoryItem(
    val instruction: String, // Stores the calculation instruction
    val result: String       // Stores the result of the calculation
) : Serializable             // To pass an ArrayList<HistoryItem> via Intent