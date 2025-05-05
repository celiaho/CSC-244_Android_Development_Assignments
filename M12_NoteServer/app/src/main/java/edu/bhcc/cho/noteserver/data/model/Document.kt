package edu.bhcc.cho.noteserver.data.model

import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("creation_date") val creationDate: String,
    @SerializedName("last_modified_date") val lastModifiedDate: String,
    @SerializedName("shared_with") val sharedWith: List<String> = emptyList()
)