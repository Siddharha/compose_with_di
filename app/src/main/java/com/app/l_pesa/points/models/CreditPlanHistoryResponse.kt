package com.app.l_pesa.points.models
import com.google.gson.annotations.SerializedName


data class CreditPlanHistoryResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("cursors")
        val cursors: Cursors,
        @SerializedName("user_buy_points_list")
        val userBuyPointsList: List<UserBuyPoints>
    ) {
        data class Cursors(
            @SerializedName("after")
            val after: String,
            @SerializedName("before")
            val before: String,
            @SerializedName("hasNext")
            val hasNext: Boolean,
            @SerializedName("hasPrevious")
            val hasPrevious: Boolean
        )

        data class UserBuyPoints(
            @SerializedName("active_manual_date")
            val activeManualDate: Any,
            @SerializedName("admin_by")
            val adminBy: Int,
            @SerializedName("admin_status")
            val adminStatus: Int,
            @SerializedName("applied_date")
            val appliedDate: String,
            @SerializedName("bp_coin_value")
            val bpCoinValue: Int,
            @SerializedName("bp_ico_type")
            val bpIcoType: Int,
            @SerializedName("bp_title")
            val bpTitle: String,
            @SerializedName("buy_point_amount")
            val buyPointAmount: Int,
            @SerializedName("buy_status")
            val buyStatus: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("currency_code")
            val currencyCode: String,
            @SerializedName("disapprove_date")
            val disapproveDate: String,
            @SerializedName("disapprove_reason")
            val disapproveReason: String,
            @SerializedName("error_code")
            val errorCode: String,
            @SerializedName("error_message")
            val errorMessage: String,
            @SerializedName("finished_date")
            val finishedDate: Any,
            @SerializedName("get_credit_score")
            val getCreditScore: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("identity_number")
            val identityNumber: String,
            @SerializedName("method_type")
            val methodType: String,
            @SerializedName("sanctioned_date")
            val sanctionedDate: Any,
            @SerializedName("saving_points_id")
            val savingPointsId: Int,
            @SerializedName("saving_points_plan")
            val savingPointsPlan: SavingPointsPlan,
            @SerializedName("tigo_submitted")
            val tigoSubmitted: Int,
            @SerializedName("transfer_amount")
            val transferAmount: Int,
            @SerializedName("txn_id")
            val txnId: String,
            @SerializedName("user_id")
            val userId: Int
        ) {
            data class SavingPointsPlan(
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
                val price: Int,
                @SerializedName("status")
                val status: String
            )
        }
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