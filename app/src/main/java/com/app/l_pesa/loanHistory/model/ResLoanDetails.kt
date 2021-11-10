package com.app.l_pesa.loanHistory.model
import com.google.gson.annotations.SerializedName


data class ResLoanDetails(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("actual_loan_amount")
        val actualLoanAmount: String,
        @SerializedName("applied_date")
        val appliedDate: String,
        @SerializedName("conversion_charge")
        val conversionCharge: String,
        @SerializedName("conversion_charge_amount")
        val conversionChargeAmount: String,
        @SerializedName("convertion_dollar_value")
        val convertionDollarValue: String,
        @SerializedName("convertion_loan_amount")
        val convertionLoanAmount: String,
        @SerializedName("cr_sc_when_requesting_loan")
        val crScWhenRequestingLoan: String,
        @SerializedName("currency_code")
        val currencyCode: String,
        @SerializedName("currency_flag")
        val currencyFlag: Boolean,
        @SerializedName("disapprove_date")
        val disapproveDate: String,
        @SerializedName("disapprove_reason")
        val disapproveReason: String,
        @SerializedName("due_date")
        val dueDate: String,
        @SerializedName("duration")
        val duration: String,
        @SerializedName("finished_date")
        val finishedDate: String,
        @SerializedName("grace_period")
        val gracePeriod: String,
        @SerializedName("identity_number")
        val identityNumber: String,
        @SerializedName("interest_rate")
        val interestRate: String,
        @SerializedName("interest_rt")
        val interestRt: String,
        @SerializedName("loan_amount")
        val loanAmount: String,
        @SerializedName("loan_amount_txt")
        val loanAmountTxt: String,
        @SerializedName("loan_id")
        val loanId: Int,
        @SerializedName("loan_purpose_message")
        val loanPurposeMessage: String,
        @SerializedName("loan_status")
        val loanStatus: String,
        @SerializedName("loan_status_txt")
        val loanStatusTxt: List<Any>,
        @SerializedName("lost_date")
        val lostDate: String,
        @SerializedName("no_of_weeks")
        val noOfWeeks: String,
        @SerializedName("processing_fees")
        val processingFees: String,
        @SerializedName("processing_fees_amount")
        val processingFeesAmount: String,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("sanctioned_date")
        val sanctionedDate: String,
        @SerializedName("total_payback")
        val totalPayback: String
    )

    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: String
    )
}