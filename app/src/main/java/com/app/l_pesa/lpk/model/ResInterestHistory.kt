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

    data class UserInterestHistory( var id: Int, var user_id: Int , var lpk_sav_his_id: Int ,
                                    var country_code: String ,var identity_number: String,var currency_code: String,var amount: String,var tokens: String, var narration: String,
                                    var interest_amount: String,var interest_token: String,var actual_tokens:String,var created: String )


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
