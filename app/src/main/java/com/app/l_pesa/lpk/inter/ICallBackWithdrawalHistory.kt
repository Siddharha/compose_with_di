package com.app.l_pesa.lpk.inter

import com.app.l_pesa.lpk.model.ResWithdrawalHistory
import java.util.ArrayList

interface ICallBackWithdrawalHistory {

    fun onSuccessWithdrawalHistory(userWithdrawalHistory: ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>)
    fun onEmptyWithdrawalHistory()
    fun onErrorWithdrawalHistory(message: String)
}