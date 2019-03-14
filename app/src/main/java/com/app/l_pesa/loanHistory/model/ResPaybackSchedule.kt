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
        var paybackSchedule: ArrayList<PaybackSchedule>? = null

    }


    inner class LoanInfo {

        @SerializedName("loan_id")
        @Expose
        var loanId: Int = 0
        @SerializedName("ref_no")
        @Expose
        var refNo: String = ""
        @SerializedName("merchant_code")
        @Expose
        var merchantCode: Int = 0
        @SerializedName("total_payback")
        @Expose
        var totalPayback: String = ""
        @SerializedName("payfullamount_button_status")
        @Expose
        var payfullamountButtonStatus: Boolean = false
        @SerializedName("payfullamount_button_text")
        @Expose
        var payfullamountButtonText: String = ""
        @SerializedName("payfullamount_message")
        @Expose
        var payfullamountMessage: String = ""

    }

    inner class PaybackSchedule {

        @SerializedName("paid_date")
        @Expose
        var paidDate: String = ""
        @SerializedName("loan_history_id")
        @Expose
        var loan_history_id: Int = 0
        @SerializedName("paid_amount")
        @Expose
        var paidAmount: String = ""
        @SerializedName("paid_status")
        @Expose
        var paidStatus: String = ""
        @SerializedName("s_date")
        @Expose
        var sDate: String = ""


    }

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
