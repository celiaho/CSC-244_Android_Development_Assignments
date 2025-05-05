package edu.bhcc.cho.noteserver.data.model

import com.google.gson.annotations.SerializedName

data class GetDocumentsResponse(
    @SerializedName("data") val documents: List<Document>,
    @SerializedName("limit") val limit: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("total") val total: Int
)
