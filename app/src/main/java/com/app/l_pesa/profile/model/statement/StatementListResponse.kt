package com.app.l_pesa.profile.model.statement
import com.google.gson.annotations.SerializedName


data class StatementListResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("created")
        val created: String,
        @SerializedName("document_number")
        val documentNumber: String,
        @SerializedName("file_name")
        val fileName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("type_id")
        val typeId: Int,
        @SerializedName("type_name")
        val typeName: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("verified")
        val verified: Int
    )

    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}