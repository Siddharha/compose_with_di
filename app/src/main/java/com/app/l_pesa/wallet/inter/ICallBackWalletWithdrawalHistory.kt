package com.app.l_pesa.wallet.inter

import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
import java.util.ArrayList

interface ICallBackWalletWithdrawalHistory {

    fun onSuccessWalletWithdrawalHistory(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors)
    fun onSuccessWalletWithdrawalHistoryPaginate(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors)
    fun onErrorWalletWithdrawalHistory(message: String)
    fun onEmptyWalletWithdrawalHistory()
}