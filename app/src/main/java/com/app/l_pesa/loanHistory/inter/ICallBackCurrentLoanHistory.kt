package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import java.util.ArrayList

interface ICallBackCurrentLoanHistory {

    fun onSuccessLoanHistory(loan_historyCurrent: ArrayList<ResLoanHistoryCurrent.LoanHistory>, cursors: ResLoanHistoryCurrent.Cursors, user_credit_score: Int, from_date: String, to_date: String)
    fun onSuccessPaginateLoanHistory(loan_historyCurrent: ArrayList<ResLoanHistoryCurrent.LoanHistory>, cursors: ResLoanHistoryCurrent.Cursors, from_date: String, to_date: String)
    fun onEmptyLoanHistory()
    fun onFailureLoanHistory(jsonMessage: String)
    fun onClickList()
}