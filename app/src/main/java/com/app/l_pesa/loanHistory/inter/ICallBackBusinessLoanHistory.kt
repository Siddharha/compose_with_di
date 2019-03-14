package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanHistoryBusiness

interface ICallBackBusinessLoanHistory {

    fun onSuccessLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors, user_credit_score: Int)
    fun onSuccessPaginateLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors)
    fun onEmptyLoanHistory()
    fun onEmptyPaginateLoanHistory()
    fun onFailureLoanHistory(jsonMessage: String)
    fun onClickList()
}