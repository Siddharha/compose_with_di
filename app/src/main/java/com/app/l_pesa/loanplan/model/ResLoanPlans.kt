package com.app.l_pesa.loanplan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class ResLoanPlans {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Item {

        @SerializedName("details")
        @Expose
        var details: Details? = null

    }

    inner class Status {

        @SerializedName("statusCode")
        @Expose
        var statusCode: Int = 0
        @SerializedName("isSuccess")
        @Expose
        var isSuccess: Boolean = false
        @SerializedName("message")
        @Expose
        var message: String = ""

    }

    inner class AppliedProduct {

        @SerializedName("loan_id")
        @Expose
        var loanId: Int = 0
        @SerializedName("identity_number")
        @Expose
        var identityNumber: String = ""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("product_id")
        @Expose
        var productId: Int = 0
        @SerializedName("loan_amount")
        @Expose
        var loanAmount: Int = 0
        @SerializedName("interest_rt")
        @Expose
        var interestRt: Double = 0.0
        @SerializedName("grace_period")
        @Expose
        var gracePeriod: String = ""
        @SerializedName("interest_rate")
        @Expose
        var interestRate: String = ""
        @SerializedName("no_of_weeks")
        @Expose
        var noOfWeeks: Int = 0
        @SerializedName("duration")
        @Expose
        var duration: String = ""
        @SerializedName("convertion_dollar_value")
        @Expose
        var convertionDollarValue: Double = 0.0
        @SerializedName("convertion_loan_amount")
        @Expose
        var convertionLoanAmount: Int = 0
        @SerializedName("actual_loan_amount")
        @Expose
        var actualLoanAmount: Int = 0
        @SerializedName("conversion_charge")
        @Expose
        var conversionCharge: String = ""
        @SerializedName("conversion_charge_amount")
        @Expose
        var conversionChargeAmount: String = ""
        @SerializedName("processing_fees")
        @Expose
        var processingFees: String = ""
        @SerializedName("processing_fees_amount")
        @Expose
        var processingFeesAmount: String = ""
        @SerializedName("loan_purpose_message")
        @Expose
        var loanPurposeMessage: String = ""
        @SerializedName("cr_sc_when_requesting_loan")
        @Expose
        var crScWhenRequestingLoan: String = ""
        @SerializedName("disapprove_reason")
        @Expose
        var disapproveReason: String = ""
        @SerializedName("total_payback")
        @Expose
        var totalPayback: String = ""
        @SerializedName("loan_status")
        @Expose
        var loanStatus: String = ""
        @SerializedName("loan_status_txt")
        @Expose
        var loanStatusTxt: ArrayList<String>? = null
        @SerializedName("applied_date")
        @Expose
        var appliedDate: String = ""
        @SerializedName("finished_date")
        @Expose
        var finishedDate: String = ""
        @SerializedName("disapprove_date")
        @Expose
        var disapproveDate: String = ""
        @SerializedName("lost_date")
        @Expose
        var lostDate: String = ""

    }

    inner class Data {

        @SerializedName("appliedProduct")
        @Expose
        var appliedProduct: AppliedProduct? = null
        @SerializedName("item")
        @Expose
        var item: ArrayList<Item>? = null

    }

    inner class Details {

        @SerializedName("product_id")
        @Expose
        var productId: Int = 0
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("required_credit_score")
        @Expose
        var requiredCreditScore: Int = 0
        @SerializedName("convertion_dollar_value")
        @Expose
        var convertionDollarValue: String = ""
        @SerializedName("convertion_loan_amount")
        @Expose
        var convertionLoanAmount: Int = 0
        @SerializedName("conversion_charge")
        @Expose
        var conversionCharge: String = ""
        @SerializedName("conversion_charge_amount")
        @Expose
        var conversionChargeAmount: String =""
        @SerializedName("processing_fees")
        @Expose
        var processingFees: String = ""
        @SerializedName("processing_fees_amount")
        @Expose
        var processingFeesAmount: String = ""
        @SerializedName("loan_amount")
        @Expose
        var loanAmount: Int = 0
        @SerializedName("loan_period")
        @Expose
        var loanPeriod: Int = 0
        @SerializedName("loan_period_type")
        @Expose
        var loanPeriodType: String = ""
        @SerializedName("loan_interest_rate")
        @Expose
        var loanInterestRate: Double = 0.0
        @SerializedName("status")
        @Expose
        var status: String = ""
        @SerializedName("btnStatus")
        @Expose
        var btnStatus: String = ""
        @SerializedName("btnColor")
        @Expose
        var btnColor: String = ""
        @SerializedName("btnHexColor")
        @Expose
        var btnHexColor: String = ""
        @SerializedName("btnText")
        @Expose
        var btnText: String = ""
        @SerializedName("alertMgs")
        @Expose
        var alertMgs: String = ""
        @SerializedName("wizardStatus")
        @Expose
        var wizardStatus: Boolean = false
        @SerializedName("wizardURL")
        @Expose
        var wizardURL: String = ""

    }

}