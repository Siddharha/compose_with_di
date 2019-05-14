package com.app.l_pesa.lpk.inter

import com.app.l_pesa.lpk.model.ResInterestHistory
import java.util.ArrayList

interface ICallBackInterestHistory {

    fun onSuccessInterestHistory(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?, from_date: String, to_date: String)
    fun onEmptyInterestHistory(type: String)
    fun onErrorInterestHistory(message: String)
    fun onSuccessInterestHistoryPaginate(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?,from_date: String, to_date: String)
}