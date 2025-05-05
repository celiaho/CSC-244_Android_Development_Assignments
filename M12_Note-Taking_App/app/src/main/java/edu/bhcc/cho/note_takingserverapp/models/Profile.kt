package edu.bhcc.cho.note_takingserverapp.models

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("creation_date") val creationDate: String,
    @SerializedName("last_modified_date") val lastModifiedDate: String,
    @SerializedName("extra") val extra: Map<String, Any> = emptyMap()
)
