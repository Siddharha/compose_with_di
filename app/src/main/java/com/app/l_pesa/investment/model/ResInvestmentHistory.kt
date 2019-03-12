package com.app.l_pesa.investment.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class ResInvestmentHistory {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("user_investment")
        @Expose
        var userInvestment: ArrayList<UserInvestment>? = null

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

    inner class UserInvestment {

        @SerializedName("investment_id")
        @Expose
        var investmentId: Int = 0
        @SerializedName("identity_number")
        @Expose
        var identityNumber: String = ""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("deposit_amount")
        @Expose
        var depositAmount: Int = 0
        @SerializedName("deposit_month")
        @Expose
        var depositMonth: Int = 0
        @SerializedName("maturity_amount")
        @Expose
        var maturityAmount: String = ""
        @SerializedName("maturity_date")
        @Expose
        var maturityDate: String = ""
        @SerializedName("applied_date")
        @Expose
        var applied_date: String = ""
        @SerializedName("interest_amount")
        @Expose
        var interestAmount: String = ""
        @SerializedName("deposit_interest_rate")
        @Expose
        var depositInterestRate: Int = 0
        @SerializedName("deposit_status")
        @Expose
        var depositStatus: String = ""
        @SerializedName("re_invest_id")
        @Expose
        var reInvestId: Int = 0
        @SerializedName("withdraw_date")
        @Expose
        var withdrawDate: String = ""
        @SerializedName("deposit_date")
        @Expose
        var depositDate: String = ""
        /*@SerializedName("interest_number")
        @Expose
        var interestNumber: Int = 0*/

    }

}
