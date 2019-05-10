package com.app.l_pesa.wallet.model

import com.app.l_pesa.common.CommonStatusModel
import java.util.ArrayList

class ResWalletWithdrawalHistory(val status: CommonStatusModel, val data: Data) {

    data class Data(var withdrawal_history: ArrayList<WithdrawalHistory>, val cursors: Cursors)

    data class Cursors(

            val hasNext: Boolean,
            val after: String
    )

    data class WithdrawalHistory(

            var id: Int,
            var status: Int,

            var withdrawal_amount: Double,
            var transfer_amount: Double,
            var commission_percentage: Double,

            var identity_number: String,
            var txn_id: String,
            var currency_code: String,
            var created: String


    )
}