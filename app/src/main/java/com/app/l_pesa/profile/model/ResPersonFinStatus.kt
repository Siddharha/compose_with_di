package com.app.l_pesa.profile.model
import com.google.gson.annotations.SerializedName


data class ResPersonFinStatus(
    @SerializedName("status")
    val status: Status
) {
    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}