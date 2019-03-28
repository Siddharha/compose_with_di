package com.app.l_pesa.lpk.inter

import com.app.l_pesa.lpk.model.ResInterestHistory
import java.util.ArrayList

interface ICallBackInterestHistory {

    fun onSuccessInterestHistory(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?)
    fun onEmptyInterestHistory()
    fun onErrorInterestHistory(message: String)
}