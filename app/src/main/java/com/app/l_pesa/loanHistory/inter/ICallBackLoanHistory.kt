package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanHistory
import java.util.ArrayList

interface ICallBackLoanHistory {

    fun onSuccessLoanHistory(loan_history: ArrayList<ResLoanHistory.LoanHistory>, cursors: ResLoanHistory.Cursors, user_credit_score: Int)
    fun onSuccessPaginateLoanHistory(loan_history: ArrayList<ResLoanHistory.LoanHistory>, cursors: ResLoanHistory.Cursors)
    fun onEmptyLoanHistory()
    fun onEmptyPaginateLoanHistory()
    fun onFailureLoanHistory(jsonMessage: String)
    fun onClickList()
}