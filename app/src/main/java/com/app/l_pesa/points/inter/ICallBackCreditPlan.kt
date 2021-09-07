package com.app.l_pesa.points.inter

import com.app.l_pesa.points.models.CreditPlanResponse

interface ICallBackCreditPlan {
    fun onCreditPlanClickList()
    fun onCreditPlanItemClickList(itm: CreditPlanResponse.Data.Plan)
}