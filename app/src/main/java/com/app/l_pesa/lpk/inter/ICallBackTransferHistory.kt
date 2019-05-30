package com.app.l_pesa.lpk.inter

import com.app.l_pesa.lpk.model.ResTransferHistory
import java.util.ArrayList

interface ICallBackTransferHistory {

    fun onSuccessTransferHistory(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors?,from_date:String,to_date:String)
    fun onSuccessTransferHistoryPaginate(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors,from_date:String,to_date:String)
    fun onEmptyTransferHistory(type: String)
    fun onErrorTransferHistory(message: String)
    fun onSessionTimeOut(message: String)
    fun onSavingsUnlock(savings_id: String)
    fun onSuccessSavingsUnlock()
    fun onErrorSavingsUnlock(message: String)
}