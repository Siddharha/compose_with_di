package com.app.l_pesa.profile.model
import com.google.gson.annotations.SerializedName


data class ResUserAdditionalInfoDropdowns(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("educationalLevels")
        val educationalLevels: List<EducationalLevel>,
        @SerializedName("incomeSources")
        val incomeSources: List<IncomeSource>,
        @SerializedName("netMonthlyIncomes")
        val netMonthlyIncomes: List<NetMonthlyIncome>
    ) {
        data class EducationalLevel(
            @SerializedName("name")
            val name: String,
            @SerializedName("value")
            val value: String
        )

        data class IncomeSource(
            @SerializedName("name")
            val name: String,
            @SerializedName("value")
            val value: String
        )

        data class NetMonthlyIncome(
            @SerializedName("name")
            val name: String,
            @SerializedName("value")
            val value: String
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