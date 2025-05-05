package edu.bhcc.cho.note_takingserverapp.models

import com.google.gson.annotations.SerializedName

data class AuthToken(
    @SerializedName("token") val token: String
)
