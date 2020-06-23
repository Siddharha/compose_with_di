package com.app.l_pesa.registration.model

import com.app.l_pesa.common.CommonStatusModel
import com.google.gson.annotations.SerializedName

data class ResVerifyCode(
    @SerializedName("status")
    val status: CommonStatusModel? = null,
    @SerializedName("data")
    val data: Data? = null
)