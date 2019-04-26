package com.app.l_pesa.loanHistory.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ResPaybackSchedule {


    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("loan_info")
        @Expose
        var loanInfo: LoanInfo? = null
        @SerializedName("schedule")
        @Expose
        var schedule: ArrayList<Schedule>? = null
        @SerializedName("cursors")
        @Expose
        var cursors: Cursors? = null

    }

    data class Cursors(

            val hasNext:Boolean,
            val after:String
    )

    inner class LoanInfo {

        @SerializedName("loan_id")
        @Expose
        var loanId: Int = 0
        @SerializedName("identity_number")
        @Expose
        var identityNumber: String = ""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("total_payback")
        @Expose
        var totalPayback: Int = 0
        @SerializedName("loan_status")
        @Expose
        var loanStatus: String = ""
        @SerializedName("loan_amount")
        @Expose
        var loanAmount: Double = 0.0
        @SerializedName("full_interest_rate")
        @Expose
        var fullInterestRate: Int = 0
        @SerializedName("discount_payanytime")
        @Expose
        var discountPayanytime: Int = 0
        @SerializedName("current_balance")
        @Expose
        var currentBalance: Int = 0
        @SerializedName("merchant_code")
        @Expose
        var merchantCode: Int = 0
        @SerializedName("payfullamount")
        @Expose
        var payfullamount: Payfullamount? = null

        @SerializedName("payment_message")
        @Expose
        var payment_message: PaymentMessage? = null

    }

    inner class PaymentMessage {

        @SerializedName("header")
        @Expose
        var header: String = ""

        @SerializedName("header2")
        @Expose
        var header2: String = ""

    }

    inner class Payanytime {

        @SerializedName("btnVisibility")
        @Expose
        var btnVisibility: Boolean = false
        @SerializedName("btnStatus")
        @Expose
        var btnStatus: String = ""
        @SerializedName("btnColor")
        @Expose
        var btnColor: String = ""
        @SerializedName("btnHexColor")
        @Expose
        var btnHexColor: String = ""
        @SerializedName("btnText")
        @Expose
        var btnText: String = ""
        @SerializedName("alertMgs")
        @Expose
        var alertMgs: String = ""
        @SerializedName("repayAmount")
        @Expose
        var repayAmount: Int = 0
        @SerializedName("mgsText")
        @Expose
        var mgsText: String = ""

    }



    data class Schedule(

        val loan_history_id:Int,
        val currency_code:String,
        var paid_amount: Double,
        var paid_by_user_amount: Int,
        var sDate: String ,
        val paid_status: String,
        var paid_date: String,
        var payanytime: Payanytime

    )

    data class Payfullamount(

        val btnStatus:Boolean,
        val loanAmount: Double,
        val btnText:String,
        val mgsText:String

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
