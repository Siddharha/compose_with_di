package com.app.l_pesa.dev_options.models
import com.google.gson.annotations.SerializedName


data class UserLocationPayload(
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
)