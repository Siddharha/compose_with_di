package com.app.l_pesa.loanHistory.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ResPaymentHistory {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("payment_history")
        @Expose
        var paymentHistory: ArrayList<PaymentHistory>? = null

    }

    data class PaymentHistory(

        var id: Int,
        var txn_id: String,
        var payment_amount: Double,
        var status: String,
        var currency_code: String,
        var created: String

    )

    inner class Status {

        @SerializedName("statusCode")
        @Expose
        var statusCode: Int = 0
        @SerializedName("isSuccess")
        @Expose
        var isSuccess: Boolean = false
        @SerializedName("message")
        @Expose
        var message: String = ""

    }
}
