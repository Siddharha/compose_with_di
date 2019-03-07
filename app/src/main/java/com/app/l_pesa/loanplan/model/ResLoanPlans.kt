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
    var data: ArrayList<Datum>? = null

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
        var isSuccess: Boolean= false
        @SerializedName("message")
        @Expose
        var message: String?= ""

    }

    inner class Details {

        @SerializedName("product_id")
        @Expose
        var productId: Int = 0
        @SerializedName("loan_amount")
        @Expose
        var loanAmount: Int = 0
        @SerializedName("required_credit_score")
        @Expose
        var requiredCreditScore: Int = 0
        @SerializedName("loan_period")
        @Expose
        var loanPeriod: Int = 0
        @SerializedName("loan_period_type")
        @Expose
        var loanPeriodType: String?= ""
        @SerializedName("loan_interest_rate")
        @Expose
        var loanInterestRate: Double = 0.0
        @SerializedName("status")
        @Expose
        var status: String?= ""
        @SerializedName("btnStatus")
        @Expose
        var btnStatus: String?= ""
        @SerializedName("btnColor")
        @Expose
        var btnColor: String?= ""
        @SerializedName("btnHexColor")
        @Expose
        var btnHexColor: String?= ""
        @SerializedName("btnText")
        @Expose
        var btnText: String?= ""
        @SerializedName("alertMgs")
        @Expose
        var alertMgs: String?= ""
        @SerializedName("wizardStatus")
        @Expose
        var wizardStatus: String?= ""
        @SerializedName("wizardURL")
        @Expose
        var wizardURL: String?= ""

    }

    inner class Datum {

        @SerializedName("appliedProduct")
        @Expose
        var appliedProduct: AppliedProduct? = null
        @SerializedName("item")
        @Expose
        var item: ArrayList<Item>? = null

    }

    inner class AppliedProduct {

        @SerializedName("identity_number")
        @Expose
        var identityNumber: String?= ""
        @SerializedName("loan_purpose_message")
        @Expose
        var loanPurposeMessage: String?= ""
        @SerializedName("applied_date")
        @Expose
        var appliedDate: String?= ""
        @SerializedName("loan_status")
        @Expose
        var loanStatus: String?= ""
        @SerializedName("product_id")
        @Expose
        var productId: Int = 0

    }
}
