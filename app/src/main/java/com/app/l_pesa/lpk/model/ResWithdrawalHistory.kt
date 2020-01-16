package com.app.l_pesa.lpk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ResWithdrawalHistory {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("user_withdrawal_history")
        @Expose
        var userWithdrawalHistory: ArrayList<UserWithdrawalHistory>? = null
        @SerializedName("cursors")
        @Expose
        var cursors: Cursors? = null

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

    data class UserWithdrawalHistory(

    val id: Int,
    val user_id: Int,
    val country_code:String,
    val identity_number:String,
    val token_value:String,
    val real_ref_no:String,
    val real_etherscan_url:String,
    val reject_reason:String,
    val ether_address:String,
    val status:String,
    val statusTxt:String,
    val created:String,
    val updated:String
    )


}
