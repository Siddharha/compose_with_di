package com.app.l_pesa.loanHistory.model
import com.google.gson.annotations.SerializedName


data class ResLoanPayment(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("CheckoutRequestID")
        val checkoutRequestID: String,
        @SerializedName("CustomerMessage")
        val customerMessage: String,
        @SerializedName("MerchantRequestID")
        val merchantRequestID: String,
        @SerializedName("ResponseCode")
        val responseCode: String,
        @SerializedName("ResponseDescription")
        val responseDescription: String
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