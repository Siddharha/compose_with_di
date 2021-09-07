package com.app.l_pesa.calculator.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*


class ResProducts {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("usd_value")
        @Expose
        var usdValue: String = ""
        @SerializedName("product_list")
        @Expose
        var productList: ArrayList<ProductList>? = null
        @SerializedName("is_dollar")
        @Expose
        var isDollar: Int = 0

    }

    inner class ProductList {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("country_code")
        @Expose
        var countryCode: String = ""
        @SerializedName("currency_code")
        @Expose
        var currencyCode: String = ""
        @SerializedName("loan_type")
        @Expose
        var loanType: String = ""
        @SerializedName("customer_type")
        @Expose
        var customerType: String = ""
        @SerializedName("required_credit_score")
        @Expose
        var requiredCreditScore: Double = 0.0
        @SerializedName("loan_amount")
        @Expose
        var loanAmount: Double = 0.0
        @SerializedName("loan_amount_txt")
        @Expose
        var loanAmountTxt: String = ""
        @SerializedName("color_code")
        @Expose
        var colorCode: String = ""
        @SerializedName("loan_amount_unit")
        @Expose
        var loanAmountUnit: String = ""
        @SerializedName("loan_interest_rate")
        @Expose
        var loanInterestRate: Double=0.0
        @SerializedName("loan_interest_rate_for_min_amount")
        @Expose
        var loanInterestRateForMinAmount: Double? = null
        @SerializedName("loan_full_interest_rate")
        @Expose
        var loanFullInterestRate: Double = 0.0
        @SerializedName("loan_period_type")
        @Expose
        var loanPeriodType: String = ""
        @SerializedName("loan_period")
        @Expose
        var loanPeriod: Int =0
        @SerializedName("processing_fees")
        @Expose
        var processingFees: Double? = null
        @SerializedName("full_processing_fees")
        @Expose
        var fullProcessingFees: Double = 0.0
        @SerializedName("coin_fees_percentage")
        @Expose
        var coinFeesPercentage: Double = 0.0
        @SerializedName("coin_fees_amount")
        @Expose
        var coinFeesAmount: Double = 0.0
        @SerializedName("discount_payanytime")
        @Expose
        var discountPayanytime: Double = 0.0
        @SerializedName("periodic_payment_amount")
        @Expose
        var periodicPaymentAmount: Double = 0.0
        @SerializedName("total_pay_back")
        @Expose
        var totalPayBack: Double = 0.0
        @SerializedName("repayment_allowed_per_day")
        @Expose
        var repaymentAllowedPerDay: Double = 0.0
        @SerializedName("insurance_coverage")
        @Expose
        var insuranceCoverage: Double = 0.0
        @SerializedName("grace_period")
        @Expose
        var gracePeriod: Double = 0.0
        @SerializedName("available_on_1")
        @Expose
        var availableOn1: Double = 0.0
        @SerializedName("available_on_2")
        @Expose
        var availableOn2: String = ""
        @SerializedName("product_apply_count")
        @Expose
        var productApplyCount: Double = 0.0
        @SerializedName("product_apply_unlimited")
        @Expose
        var productApplyUnlimited: Double = 0.0
        @SerializedName("automatic_approve_status")
        @Expose
        var automaticApproveStatus: Double = 0.0
        @SerializedName("status")
        @Expose
        var status: String = ""
        @SerializedName("is_delete")
        @Expose
        var isDelete: Double = 0.0
        @SerializedName("is_starter_loan")
        @Expose
        var isStarterLoan: Double = 0.0
        @SerializedName("created_time")
        @Expose
        var createdTime: Double = 0.0
        @SerializedName("added_by")
        @Expose
        var addedBy: Double = 0.0
        @SerializedName("discount_due")
        @Expose
        var discountDue: String = ""


    }

    inner class Status {

        @SerializedName("statusCode")
        @Expose
        var statusCode: Int = 0
        @SerializedName("isSuccess")
        @Expose
        var isSuccess: Boolean? = null
        @SerializedName("message")
        @Expose
        var message: String = ""
    }

}
