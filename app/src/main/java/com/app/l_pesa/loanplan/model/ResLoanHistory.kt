package com.app.l_pesa.loanplan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by Intellij Amiya on 21/2/19.
 * Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class ResLoanHistory {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class LoanHistory {

        @SerializedName("loan_id")
        @Expose
        var loanId: Int = 0
        @SerializedName("identity_number")
        @Expose
        var identityNumber: String=""
        @SerializedName("loan_amount")
        @Expose
        var loanAmount: Int = 0
        @SerializedName("interest_rate")
        @Expose
        var interestRate:String=""
        @SerializedName("convertion_dollar_value")
        @Expose
        var convertionDollarValue: String=""
        @SerializedName("convertion_loan_amount")
        @Expose
        var convertionLoanAmount: String=""
        @SerializedName("actual_loan_amount")
        @Expose
        var actualLoanAmount: String=""
        @SerializedName("applied_date")
        @Expose
        var appliedDate: String=""
        @SerializedName("sanctioned_date")
        @Expose
        var sanctionedDate: String=""
        @SerializedName("finished_date")
        @Expose
        var finishedDate: String=""
        @SerializedName("disapprove_date")
        @Expose
        var disapproveDate: String=""
        @SerializedName("loan_status")
        @Expose
        var loanStatus: String=""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String=""
        @SerializedName("loan_status_txt")
        @Expose
        var loanStatusTxt: ArrayList<String>? = null
        @SerializedName("due_date")
        @Expose
        var dueDate: String=""

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
        var message: String=""

    }

    inner class Data {

        @SerializedName("loan_history")
        @Expose
        var loanHistory: ArrayList<LoanHistory>? = null
        @SerializedName("user_credit_score")
        @Expose
        var userCreditScore: Int = 0

    }


}
