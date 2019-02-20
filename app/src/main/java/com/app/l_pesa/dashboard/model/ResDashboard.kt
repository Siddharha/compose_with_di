package com.app.l_pesa.dashboard.model

import android.support.annotation.Keep
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
   
    var status: Status? = null
    @SerializedName("data")
   
    var data: Data? = null

    @Keep
    inner class Loan {

        @SerializedName("type")
       
        var type: String? = null
        @SerializedName("name")
       
        var name: String = ""
        @SerializedName("status")
       
        var status: String = ""
        @SerializedName("repay")
       
        var repay: Repay? = null
        @SerializedName("next_repay")
       
        var nextRepay: NextRepay? = null

    }

    @Keep
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

    @Keep
    inner class Repay {

        @SerializedName("amount")
       
        var amount: String = ""
        @SerializedName("left")
       
        var left: Int = 0
        @SerializedName("done")
       
        var done: Int = 0
        @SerializedName("total")
       
        var total: Int = 0

    }

    @Keep
    inner class Status {

        @SerializedName("statusCode")
       
        var statusCode: Int? = null
        @SerializedName("isSuccess")
       
        var isSuccess: Boolean? = null
        @SerializedName("message")
       
        var message: String = ""

    }

    @Keep
    inner class Data {

        @SerializedName("fixed_deposit_amount")
       
        var fixedDepositAmount: String = ""
        @SerializedName("savings_amount")
       
        var savingsAmount: String = ""
        @SerializedName("credit_score")
       
        var creditScore: Int = 0
        @SerializedName("max_credit_score")
       
        var maxCreditScore: Int = 0
        @SerializedName("min_credit_score")
       
        var minCreditScore: Int = 0
        @SerializedName("loans")
       
        var loans: ArrayList<Loan>? = null

    }

}
