package edu.bhcc.cho.noteserver.data.model

import com.google.gson.annotations.SerializedName

data class SetSharersRequest(
    @SerializedName("shared_with") val sharedWith: List<String> // List of Profile IDs (dashless)
)
