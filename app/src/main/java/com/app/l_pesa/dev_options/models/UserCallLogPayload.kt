package com.app.l_pesa.dev_options.models
import com.google.gson.annotations.SerializedName


data class UserCallLogPayload(
    @SerializedName("contact_name")
    val contactName: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("time_stamp")
    val timeStamp: String,
    @SerializedName("type")
    val type: String
)