package com.app.l_pesa.dashboard.model

import android.support.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by Intellij Amiya on 20-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
@Keep
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
        var type: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("status")
        @Expose
        var status: String? = null
        @SerializedName("repay")
        @Expose
        var repay: Repay? = null
        @SerializedName("next_repay")
        @Expose
        var nextRepay: NextRepay? = null

    }

    inner class NextRepay {

        @SerializedName("date")
        @Expose
        var date: String? = null
        @SerializedName("amount")
        @Expose
        var amount: String? = null
        @SerializedName("left_days")
        @Expose
        var leftDays: Int? = null
        @SerializedName("left_days_text")
        @Expose
        var leftDaysText: String? = null

    }

    inner class Repay {

        @SerializedName("amount")
        @Expose
        var amount: String? = null
        @SerializedName("left")
        @Expose
        var left: Int? = null
        @SerializedName("done")
        @Expose
        var done: Int? = null
        @SerializedName("total")
        @Expose
        var total: Int? = null

    }

    inner class Status {

        @SerializedName("statusCode")
        @Expose
        var statusCode: Int? = null
        @SerializedName("isSuccess")
        @Expose
        var isSuccess: Boolean? = null
        @SerializedName("message")
        @Expose
        var message: String? = null

    }

    inner class Data {

        @SerializedName("fixed_deposit_amount")
        @Expose
        var fixedDepositAmount: String? = null
        @SerializedName("savings_amount")
        @Expose
        var savingsAmount: String? = null
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

    }

}
