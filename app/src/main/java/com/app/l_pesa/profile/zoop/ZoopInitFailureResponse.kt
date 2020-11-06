package com.app.l_pesa.profile.zoop
import com.google.gson.annotations.SerializedName


data class ZoopInitFailureResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int
)