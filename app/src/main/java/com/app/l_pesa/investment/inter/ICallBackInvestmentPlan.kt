package com.app.l_pesa.investment.inter

import com.app.l_pesa.investment.model.ResInvestmentPlan

interface ICallBackInvestmentPlan {

    fun onSuccessInvestmentPlan(data: ResInvestmentPlan.Data)
    fun onEmptyInvestmentPlan()
    fun onErrorInvestmentPlan(jsonMessage: String)
    fun onSessionTimeOut(jsonMessage: String)
    fun onClickInvestmentPlan(investmentPlan: ResInvestmentPlan.InvestmentPlan)
}