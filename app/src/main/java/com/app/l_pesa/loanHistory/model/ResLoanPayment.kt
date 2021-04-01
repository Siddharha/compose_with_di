package com.app.l_pesa.loanHistory.model
import com.google.gson.annotations.SerializedName


data class ResLoanPayment(
    @SerializedName("status")
    val status: Status
) {
    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}