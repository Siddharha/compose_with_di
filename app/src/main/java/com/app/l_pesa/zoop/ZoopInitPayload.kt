package com.app.l_pesa.zoop
import com.google.gson.annotations.SerializedName


data class ZoopInitPayload(
    @SerializedName("face_match")
    val faceMatch: String,
    @SerializedName("purpose")
    val purpose: String,
    @SerializedName("response_url")
    val responseUrl: String
)