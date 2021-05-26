package com.app.l_pesa.dev_options.models
import com.google.gson.annotations.SerializedName


data class UserSMSPayload(
    @SerializedName("sms_object")
    val smsObject: SmsObject
) {
    data class SmsObject(
        @SerializedName("message")
        val message: String,
        @SerializedName("sender")
        val sender: String,
        @SerializedName("sms_date")
        val smsDate: String,
        @SerializedName("subject")
        val subject: String
    )
}