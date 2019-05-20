package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanHistoryBusiness

interface ICallBackBusinessLoanHistory {

    fun onSuccessLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors, user_credit_score: Int, from_date: String, to_date: String)
    fun onSuccessPaginateLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors, from_date: String, to_date: String)
    fun onEmptyLoanHistory(type: String)
    fun onFailureLoanHistory(jsonMessage: String)
    fun onClickList()
    fun onRemoveLoan(position: Int, loanHistoryBusiness: ResLoanHistoryBusiness.LoanHistory)

    fun onSuccessRemoveLoan(position: Int)
    fun onFailureRemoveLoan(message: String)

}