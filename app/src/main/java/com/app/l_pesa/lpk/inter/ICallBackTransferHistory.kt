package com.app.l_pesa.lpk.inter

import com.app.l_pesa.lpk.model.ResTransferHistory
import java.util.ArrayList

interface ICallBackTransferHistory {

    fun onSuccessTransferHistory(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors?)
    fun onSuccessTransferHistoryPaginate(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors)
    fun onEmptyTransferHistory()
    fun onErrorTransferHistory(message: String)
    fun onSavingsUnlock(savings_id: String)
    fun onSuccessSavingsUnlock()
    fun onErrorSavingsUnlock(message: String)
}