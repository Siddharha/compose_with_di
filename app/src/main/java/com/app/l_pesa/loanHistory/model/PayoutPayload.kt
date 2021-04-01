package com.app.l_pesa.loanHistory.model
import com.google.gson.annotations.SerializedName


data class PayoutPayload(
    @SerializedName("payment_details")
    val paymentDetails: PaymentDetails
) {
    data class PaymentDetails(
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("bill_ref_number")
        val billRefNumber: String,
        @SerializedName("msisdn")
        val msisdn: String
    )
}