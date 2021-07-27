package com.app.l_pesa.points.models
import com.google.gson.annotations.SerializedName


data class ApplyCreditPlanPayload(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("buy_cr_sc_id")
    val buyCrScId: String
)