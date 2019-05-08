package com.app.l_pesa.wallet.inter

import com.app.l_pesa.wallet.model.ResWalletHistory
import java.util.ArrayList

interface ICallBackTransaction {

    fun onSuccessTransaction(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>, cursors: ResWalletHistory.Cursors, from_date: String, to_date: String)
    fun onSuccessTransactionPaginate(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>, cursors: ResWalletHistory.Cursors, from_date: String, to_date: String)
    fun onEmptyTransaction()
    fun onErrorTransaction(message: String)
}