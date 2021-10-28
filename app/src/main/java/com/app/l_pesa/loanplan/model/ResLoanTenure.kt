package com.app.l_pesa.loanplan.model
import com.google.gson.annotations.SerializedName


data class ResLoanTenure(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("options")
        val options: List<Option>,
        @SerializedName("product_id")
        val productId: Int
    ) {
        data class Option(
            @SerializedName("days")
            val days: Int,
            @SerializedName("default")
            val default: Boolean,
            @SerializedName("due")
            val due: String,
            @SerializedName("weeks")
            val weeks: Int
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