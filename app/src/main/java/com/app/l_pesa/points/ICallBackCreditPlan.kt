package com.app.l_pesa.points

import com.app.l_pesa.points.view.CreditPlanResponse

interface ICallBackCreditPlan {
    fun onCreditPlanClickList()
    fun onCreditPlanItemClickList(itm:CreditPlanResponse.Data.Plan)
}