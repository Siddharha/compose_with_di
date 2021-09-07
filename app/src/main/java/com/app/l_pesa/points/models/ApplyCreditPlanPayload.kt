package com.app.l_pesa.points.models
import com.google.gson.annotations.SerializedName


data class ApplyCreditPlanPayload(
    @SerializedName("buy_point_id")
    val buyPointId: String
)