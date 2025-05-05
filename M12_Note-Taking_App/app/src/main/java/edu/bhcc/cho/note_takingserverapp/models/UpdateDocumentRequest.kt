package edu.bhcc.cho.note_takingserverapp.models

import com.google.gson.annotations.SerializedName

data class UpdateDocumentRequest(
    @SerializedName("content") val content: Map<String, Any> // Content can be any valid JSON
)
