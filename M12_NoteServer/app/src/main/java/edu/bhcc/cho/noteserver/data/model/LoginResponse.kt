package edu.bhcc.cho.noteserver.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String
)
