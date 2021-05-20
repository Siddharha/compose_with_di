package com.app.l_pesa.allservices.models
import com.google.gson.annotations.SerializedName


data class SasaPaymentResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("pay_bill")
        val payBill: Int,
        @SerializedName("ref_no")
        val refNo: String,
        @SerializedName("serial_number")
        val serialNumber: String
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