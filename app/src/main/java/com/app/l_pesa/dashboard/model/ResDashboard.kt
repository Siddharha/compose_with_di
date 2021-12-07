package com.app.l_pesa.dashboard.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*


class ResDashboard {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Loan {

        @SerializedName("type")
        @Expose
        var type: String = ""
        @SerializedName("name")
        @Expose
        var name: String = ""
        @SerializedName("status")
        @Expose
        var status: String = ""
        @SerializedName("coins")
        @Expose
        var coins: String = ""
        @SerializedName("coin_cash")
        @Expose
        var coinCash: String = ""
        var repay: Repay? = null
        @SerializedName("next_repay")

        var nextRepay: NextRepay? = null

    }

    inner class PersonalIdType {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("name")
        @Expose
        var name: String = ""
        @SerializedName("type")
        @Expose
        var type: String = ""

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

    inner class NextRepay {
        @SerializedName("date")

        var date: String = ""
        @SerializedName("amount")

        var amount: Double =0.0
        @SerializedName("left_days")

        var leftDays: Int = 0
        @SerializedName("left_days_text")

        var leftDaysText: String = ""
    }

    inner class Repay {

        @SerializedName("currency")
        var currency: String = ""

        @SerializedName("amount")
        var amount: Double =0.0

        @SerializedName("left")
        var left: Int = 0

        @SerializedName("done")
        var done: Int = 0

        @SerializedName("total")
        var total: Int = 0

        @SerializedName("loan_id")
        var loan_id: Int = 0
    }

    inner class BusinessIdType {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("name")
        @Expose
        var name: String = ""
        @SerializedName("type")
        @Expose
        var type: String = ""

    }

    inner class Data {

        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("wallet_address")
        @Expose
        var walletAddress: String = ""
        @SerializedName("saving_invest_auto_status")
        @Expose
        var savingInvestAutoStatus: Int = 0
        @SerializedName("fixed_deposit_amount")
        @Expose
        var fixedDepositAmount: Double = 0.0
        @SerializedName("savings_amount")
        @Expose
        var savingsAmount: Double = 0.0
        @SerializedName("credit_score")
        @Expose
        var creditScore: Int = 0
        @SerializedName("max_credit_score")
        @Expose
        var maxCreditScore: Int = 0
        @SerializedName("min_credit_score")
        @Expose
        var minCreditScore: Int = 0
        @SerializedName("lpk_value")
        @Expose
        var lpk_value: Double = 0.0
        @SerializedName("wallet_balance")
        @Expose
        var wallet_balance: Double = 0.0
        @SerializedName("commission_eachtime")
        @Expose
        var commission_eachtime: Double = 0.0
        @SerializedName("loans")
        @Expose
        var loans: ArrayList<Loan>? = null
        @SerializedName("personal_id_types")
        @Expose
        var personalIdTypes: ArrayList<PersonalIdType>? = null
        @SerializedName("business_id_types")
        @Expose
        var businessIdTypes: ArrayList<BusinessIdType>? = null
        @SerializedName("profile_complete_percentage")
        @Expose
        var profileCompletePercentage: Int?=0

        @SerializedName("loan_eligibility")
        @Expose
        var loanEligibility: Boolean?=false

    }

}
