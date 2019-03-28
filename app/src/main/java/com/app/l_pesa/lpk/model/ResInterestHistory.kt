package com.app.l_pesa.lpk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class ResInterestHistory {

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

    inner class UserInterestHistory {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("user_id")
        @Expose
        var userId: Int = 0
        @SerializedName("lpk_sav_his_id")
        @Expose
        var lpkSavHisId: Int = 0
        @SerializedName("country_code")
        @Expose
        var countryCode: String = ""
        @SerializedName("identity_number")
        @Expose
        var identityNumber: String = ""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("amount")
        @Expose
        var amount: String = ""
        @SerializedName("tokens")
        @Expose
        var tokens: String = ""
        @SerializedName("narration")
        @Expose
        var narration: String = ""
        @SerializedName("interest_amount")
        @Expose
        var interestAmount: String = ""
        @SerializedName("interest_token")
        @Expose
        var interestToken: String = ""
        @SerializedName("created")
        @Expose
        var created: String = ""

    }

    inner class Cursors {

        @SerializedName("hasNext")
        @Expose
        var hasNext: Boolean = false
        @SerializedName("hasPrevious")
        @Expose
        var hasPrevious: Boolean = false
        @SerializedName("before")
        @Expose
        var before: String = ""
        @SerializedName("after")
        @Expose
        var after: String = ""

    }

    inner class Data {

        @SerializedName("user_interest_history")
        @Expose
        var userInterestHistory: ArrayList<UserInterestHistory>? = null
        @SerializedName("cursors")
        @Expose
        var cursors: Cursors? = null

    }


}
