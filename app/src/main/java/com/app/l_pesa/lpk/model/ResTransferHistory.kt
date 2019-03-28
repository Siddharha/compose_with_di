package com.app.l_pesa.lpk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class ResTransferHistory {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("user_transfer_history")
        @Expose
        var userTransferHistory: ArrayList<UserTransferHistory>? = null

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

    data class UserTransferHistory(

       var id: Int,
       var user_id: Int ,
       var identity_number: String,
       var tokens: String ,
       var status: String ,
       var created: String ,
       var updated: String

    )


}
