package com.app.l_pesa.wallet.model

import com.app.l_pesa.common.CommonStatusModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

 class ResWalletHistory(val status: CommonStatusModel, val data: Data) {

    data class Data(var savings_history: ArrayList<SavingsHistory>, val cursors:Cursors)

    data class Cursors(

            val hasNext:Boolean,
            val after:String
    )

    data class SavingsHistory(

        var id: Int,
        var user_id: Int,

        var interest_rate: Double,
        var debit_amount: Double,
        var credit_amount: Double,
        var closing_amount: Double,

        var type_id: String,
        var type_ref_no: String,
        var type_name: String,
        var reference_number: String,
        var country_code: String,
        var narration: String,
        var currency_code: String,
        var created: String

    )



}
