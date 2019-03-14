package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import java.util.ArrayList

interface ICallBackCurrentLoanHistory {

    fun onSuccessLoanHistory(loan_historyCurrent: ArrayList<ResLoanHistoryCurrent.LoanHistory>, cursors: ResLoanHistoryCurrent.Cursors, user_credit_score: Int)
    fun onSuccessPaginateLoanHistory(loan_historyCurrent: ArrayList<ResLoanHistoryCurrent.LoanHistory>, cursors: ResLoanHistoryCurrent.Cursors)
    fun onEmptyLoanHistory()
    fun onEmptyPaginateLoanHistory()
    fun onFailureLoanHistory(jsonMessage: String)
    fun onClickList()
}