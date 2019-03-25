package com.app.l_pesa.investment.inter

interface ICallBackLoanPlanList {

    fun onSelectLoan(planId: Int, planName: String)
    fun onSuccessApplyInvestment()
    fun onErrorApplyInvestment(jsonMessage: String)
}