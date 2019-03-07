package com.app.l_pesa.investment.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class ResInvestmentPlan {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null


    inner class InvestmentPlan {

        @SerializedName("investment_id")
        @Expose
        var investmentId: Int = 0
        @SerializedName("country_code")
        @Expose
        var countryCode: String = ""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("plan_name")
        @Expose
        var planName: String = ""
        @SerializedName("deposit_month")
        @Expose
        var depositMonth: Int = 0
        @SerializedName("deposit_interest_rate")
        @Expose
        var depositInterestRate: Int = 0
        @SerializedName("minimum_credit_score")
        @Expose
        var minimumCreditScore: Int = 0
        @SerializedName("minimum_invest_amount")
        @Expose
        var minimumInvestAmount: Int = 0
        @SerializedName("maximum_invest_amount")
        @Expose
        var maximumInvestAmount: Int = 0
        @SerializedName("exit_point_penalty_percentage")
        @Expose
        var exitPointPenaltyPercentage: Double = 0.0
        @SerializedName("status")
        @Expose
        var status: String = ""
        @SerializedName("created")
        @Expose
        var created: String = ""

    }

    inner class Status {

        @SerializedName("statusCode")
        @Expose
        var statusCode: Int= 0
        @SerializedName("isSuccess")
        @Expose
        var isSuccess: Boolean= false
        @SerializedName("message")
        @Expose
        var message: String = ""
    }

    inner class Data {

        @SerializedName("investment_plans")
        @Expose
        var investmentPlans: ArrayList<InvestmentPlan>? = null
        @SerializedName("deposit_info")
        @Expose
        var depositInfo: DepositInfo? = null

    }

    inner class DepositInfo {

        @SerializedName("btnShow")
        @Expose
        var btnShow: String = ""
        @SerializedName("btnStatus")
        @Expose
        var btnStatus: String = ""
        @SerializedName("btnColor")
        @Expose
        var btnColor: String = ""
        @SerializedName("btnHexColor")
        @Expose
        var btnHexColor: String = ""
        @SerializedName("alertMessage")
        @Expose
        var alertMessage: String = ""
        @SerializedName("confirmMessage")
        @Expose
        var confirmMessage: String = ""

    }
}
