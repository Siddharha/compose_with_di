package com.app.l_pesa.profile.model.statement
import com.google.gson.annotations.SerializedName


data class StatementTypeResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("statement_types")
        val statementTypes: List<StatementType>
    ) {
        data class StatementType(
            @SerializedName("id")
            val id: Int,
            @SerializedName("type")
            val type: String,
            @SerializedName("type_name")
            val typeName: String
        )
    }

    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}