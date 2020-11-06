package com.app.l_pesa.profile.zoop
import com.google.gson.annotations.SerializedName


data class ZoopInitResponse(
    @SerializedName("env")
    val env: Int,
    @SerializedName("face_match")
    val faceMatch: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("response_url")
    val responseUrl: String,
    @SerializedName("webhook_security_key")
    val webhookSecurityKey: String
)