package com.app.l_pesa.wallet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class ResWalletHistory {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("savings_history")
        @Expose
        var savingsHistory: ArrayList<SavingsHistory>? = null

    }

    data class SavingsHistory(

        var id: Int,
        var user_id: Int,

        var interest_rate: Double,
        var debit_amount: Double,
        var credit_amount: Double,
        var closing_amount: Double,

        var type_id: String,
        var type_ref_no: String,
        var type_name: String,
        var reference_number: String,
        var country_code: String,
        var narration: String,
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
