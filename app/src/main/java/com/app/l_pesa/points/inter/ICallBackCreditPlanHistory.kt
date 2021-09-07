package com.app.l_pesa.points.inter

import com.app.l_pesa.points.models.CreditPlanHistoryResponse


interface ICallBackCreditPlanHistory {
    //fun onHistoryCreditPlanClickList()
   // fun onHistoryCreditPlanItemClickList(itm: CreditPlanHistoryResponse.Data.UserBuyPoints)
    fun onSuccessCreditHistory(loan_historyCredit: List<CreditPlanHistoryResponse.Data.UserBuyPoints>, cursors: CreditPlanHistoryResponse.Data.Cursors, from_date: String, to_date: String)
    fun onSuccessPaginateCreditHistory(loan_historyCredit: List<CreditPlanHistoryResponse.Data.UserBuyPoints>, cursors: CreditPlanHistoryResponse.Data.Cursors, from_date: String, to_date: String)
    fun onEmptyCreditHistory(type: String)
    fun onFailureLoanHistory(jsonMessage: String)
    fun onClickList()
    fun onRemoveCredit(position: Int, loanHistoryCredit: CreditPlanHistoryResponse.Data.UserBuyPoints)

    fun onSuccessRemoveCredit(position: Int)
    fun onFailureRemoveCredit(message: String)
    fun onSessionTimeOut(message: String)
}