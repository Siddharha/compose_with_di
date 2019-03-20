package com.app.l_pesa.dashboard.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by Intellij Amiya on 20-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

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

        var amount: String = ""
        @SerializedName("left_days")

        var leftDays: Int = 0
        @SerializedName("left_days_text")

        var leftDaysText: String = ""
    }

    inner class Repay {
        @SerializedName("amount")

        var amount: String = ""
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

        @SerializedName("fixed_deposit_amount")
        @Expose
        var fixedDepositAmount: String = ""
        @SerializedName("savings_amount")
        @Expose
        var savingsAmount: String = ""
        @SerializedName("credit_score")
        @Expose
        var creditScore: Int = 0
        @SerializedName("max_credit_score")
        @Expose
        var maxCreditScore: Int = 0
        @SerializedName("min_credit_score")
        @Expose
        var minCreditScore: Int = 0
        @SerializedName("loans")
        @Expose
        var loans: ArrayList<Loan>? = null
        @SerializedName("personal_id_types")
        @Expose
        var personalIdTypes: ArrayList<PersonalIdType>? = null
        @SerializedName("business_id_types")
        @Expose
        var businessIdTypes: ArrayList<BusinessIdType>? = null

    }

}
