package com.app.l_pesa.wallet.inter

import com.app.l_pesa.wallet.model.ResWalletHistory
import java.util.ArrayList

interface ICallBackTransaction {

    fun onSuccessTransaction(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>, cursors: ResWalletHistory.Cursors)
    fun onSuccessTransactionPaginate(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>, cursors: ResWalletHistory.Cursors)
    fun onEmptyTransaction()
    fun onErrorTransaction(message: String)
}