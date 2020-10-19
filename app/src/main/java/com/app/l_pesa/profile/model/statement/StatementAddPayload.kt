package com.app.l_pesa.profile.model.statement
import com.google.gson.annotations.SerializedName


data class StatementAddPayload(
    @SerializedName("document_number")
    val documentNumber: String,
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("type_id")
    val typeId: String
)