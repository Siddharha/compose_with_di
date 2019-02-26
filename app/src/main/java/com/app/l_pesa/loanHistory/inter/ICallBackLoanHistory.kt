package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanHistory
import java.util.ArrayList

interface ICallBackLoanHistory {

    fun onSuccessLoanHistory(loanHistory: ArrayList<ResLoanHistory.LoanHistory>)
    fun onEmptyLoanHistory()
    fun onFailureLoanHistory(jsonMessage: String)
}