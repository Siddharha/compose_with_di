package com.app.l_pesa.zoop
import com.google.gson.annotations.SerializedName


data class ZoopInitResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("response")
        val response: Response
    ) {
        data class Response(
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
    }

    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}