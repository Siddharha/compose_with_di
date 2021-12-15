package com.app.l_pesa.user_device_data.models
import com.google.gson.annotations.SerializedName


data class UserLocationPayload(
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
)