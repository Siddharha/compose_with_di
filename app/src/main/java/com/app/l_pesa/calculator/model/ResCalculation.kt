package com.app.l_pesa.calculator.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*


data class ResCalculation(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("current_rate")
        val currentRate: String,
        @SerializedName("each_loan_amount")
        val eachLoanAmount: String,
        @SerializedName("first_repay_min_amount")
        val firstRepayMinAmount: String,
        @SerializedName("first_weekly_payment")
        val firstWeeklyPayment: String,
        @SerializedName("group_members")
        val groupMembers: String,
        @SerializedName("last_weekly_payment")
        val lastWeeklyPayment: String,
        @SerializedName("loan_amount")
        val loanAmount: String,
        @SerializedName("loan_interest_rate")
        val loanInterestRate: String,
        @SerializedName("loan_period")
        val loanPeriod: String,
        @SerializedName("mid_weekly_payment")
        val midWeeklyPayment: String,
        @SerializedName("periodic_payment_amount")
        val periodicPaymentAmount: String,
        @SerializedName("required_admin_credit_score")
        val requiredAdminCreditScore: String,
        @SerializedName("required_credit_score")
        val requiredCreditScore: Int,
        @SerializedName("t")
        val t: String,
        @SerializedName("total_pay_back")
        val totalPayBack: String,
        @SerializedName("total_pay_back_installment")
        val totalPayBackInstallment: String,
        @SerializedName("total_repayment")
        val totalRepayment: String
    )

    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}