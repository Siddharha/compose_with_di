package com.app.l_pesa.splash.model

import com.app.l_pesa.common.CommonStatusModel
import com.google.gson.annotations.SerializedName

data class ResVersionCheck(
    @SerializedName("status")
    val commonStatusModel: CommonStatusModel
)