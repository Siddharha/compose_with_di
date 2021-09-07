package com.app.l_pesa.points.models
import com.google.gson.annotations.SerializedName


data class CreditPlanResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("plans")
        val plans: List<Plan>
    ) {
        data class Plan(
            @SerializedName("coin_value")
            val coinValue: Int,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("created")
            val created: String,
            @SerializedName("credit_score")
            val creditScore: Int,
            @SerializedName("description")
            val description: String,
            @SerializedName("ico_type")
            val icoType: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("method_type")
            val methodType: String,
            @SerializedName("modified")
            val modified: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("point_title")
            val pointTitle: String,
            @SerializedName("price")
            val price: String,
            @SerializedName("status")
            val status: String
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