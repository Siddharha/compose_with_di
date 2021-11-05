package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanHistory.model.ResLoanDetails
import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import com.app.l_pesa.loanplan.model.ResLoanPlans
import java.util.*

interface ICallBackLoanDetails {
    fun onFailureLoanDetails(jsonMessage: String)
    fun onSessionTimeOut(message: String)
    fun onSuccessLoanPlansDetails(details: ResLoanDetails.Data)
}