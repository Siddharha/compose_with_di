package com.app.l_pesa.investment.inter

import com.app.l_pesa.investment.model.ResInvestmentPlan
import java.util.ArrayList

interface ICallBackInvestmentPlan {

    fun onSuccessInvestmentPlan(investmentPlans: ArrayList<ResInvestmentPlan.InvestmentPlan>)
    fun onEmptyInvestmentPlan()
    fun onErrorInvestmentPlan(jsonMessage: String)
}