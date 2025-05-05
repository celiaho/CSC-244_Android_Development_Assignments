package edu.bhcc.cho.note_takingserverapp.models

import com.google.gson.annotations.SerializedName

data class APIError(
    @SerializedName("error") val error: String
)
