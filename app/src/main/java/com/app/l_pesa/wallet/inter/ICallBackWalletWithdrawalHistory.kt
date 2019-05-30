package com.app.l_pesa.wallet.inter

import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
import java.util.ArrayList

interface ICallBackWalletWithdrawalHistory {

    fun onSuccessWalletWithdrawalHistory(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors, from_date: String, to_date: String)
    fun onSuccessWalletWithdrawalHistoryPaginate(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors, from_date: String, to_date: String)
    fun onErrorWalletWithdrawalHistory(message: String)
    fun onSessionTimeOut(message: String)
    fun onEmptyWalletWithdrawalHistory(type: String)
}