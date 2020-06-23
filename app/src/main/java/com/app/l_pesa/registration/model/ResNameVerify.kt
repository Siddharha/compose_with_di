package com.app.l_pesa.registration.model

import com.app.l_pesa.common.CommonStatusModel
import com.google.gson.annotations.SerializedName

data class ResNameVerify(
    @SerializedName("status")
    val commonStatusModel: CommonStatusModel? = null
)