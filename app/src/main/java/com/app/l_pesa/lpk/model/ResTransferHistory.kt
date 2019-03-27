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

    inner class UserTransferHistory {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("user_id")
        @Expose
        var userId: Int = 0
        @SerializedName("identity_number")
        @Expose
        var identityNumber: String = ""
        @SerializedName("tokens")
        @Expose
        var tokens: String = ""
        @SerializedName("status")
        @Expose
        var status: String = ""
        @SerializedName("created")
        @Expose
        var created: String = ""
        @SerializedName("updated")
        @Expose
        var updated: String = ""

    }


}
