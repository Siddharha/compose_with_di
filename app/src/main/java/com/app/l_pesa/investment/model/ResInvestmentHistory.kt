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
        @SerializedName("cursors")
        @Expose
        var cursors: Cursors? = null
    }

    data class Cursors(

            val hasNext:Boolean,
            val after:String
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

    data class UserInvestment(

            val investment_id:Int,
            val interest_number:Int,
            val deposit_month:Int,
            val re_invest_id:Int,

            val identity_number:String,
            val currency_code:String,
            val deposit_status:String,
            val deposit_status_txt:String,
            val applied_date:String,
            val deposit_date:String,
            val maturity_date:String,
            val withdraw_date:String,
            val belowMessage:String,

            val deposit_amount:Double,
            val maturity_amount:Double,
            val interest_amount:Double,
            val deposit_interest_rate:Double,

            val actionState:ActionState



    )

    data class ActionState(

            val btnWithdrawalShow:Boolean,
            val btnReinvestShow:Boolean,
            val btnExitPointShow:Boolean,

            val btnExitPointStatus:String,
            val btnExitPointStatusMessage:String
    )


}
