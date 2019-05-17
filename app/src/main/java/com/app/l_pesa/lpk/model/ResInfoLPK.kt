package com.app.l_pesa.lpk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResInfoLPK {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

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

    inner class Data {

        @SerializedName("lpk_savings_text")
        @Expose
        var lpkSavingsText: String = ""
        @SerializedName("lpk_value")
        @Expose
        var lpkValue: Double = 0.0
        @SerializedName("lpk_locked_value")
        @Expose
        var lpkLockedValue: Double = 0.0
        @SerializedName("lpk_min_withdrawal")
        @Expose
        var lpkMinWithdrawal: Double = 0.0
        @SerializedName("wallet_balance")
        @Expose
        var wallet_balance: Double = 0.0
        @SerializedName("commission_eachtime")
        @Expose
        var commission_eachtime: Double = 0.0

    }


}
