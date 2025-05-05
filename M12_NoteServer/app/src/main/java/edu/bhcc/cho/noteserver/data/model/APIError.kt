package edu.bhcc.cho.noteserver.data.model

import com.google.gson.annotations.SerializedName

data class APIError(
    @SerializedName("error") val error: String
)
