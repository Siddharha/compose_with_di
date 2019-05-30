package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanplan.model.ResLoanPlans
import java.util.ArrayList

interface ICallBackBusinessLoan {

    fun onSuccessLoanPlans(item: ArrayList<ResLoanPlans.Item>, appliedProduct: ResLoanPlans.AppliedProduct?)
    fun onEmptyLoanPlans()
    fun onFailureLoanPlans(jsonMessage: String)
    fun onSessionTimeOut(message: String)
    fun onSuccessLoanPlansDetails(details: ResLoanPlans.Details?)
    fun onSuccessLoanHistory()
}