package edu.bhcc.cho.noteserver.data.model

import com.google.gson.annotations.SerializedName

data class AuthToken(
    @SerializedName("token") val token: String
)
