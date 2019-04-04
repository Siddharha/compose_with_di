package com.app.l_pesa.investment.view

import com.app.l_pesa.investment.model.ResInvestmentPlan

class GlobalInvestmentPlanData

    private constructor() {
        var modelData: ResInvestmentPlan.InvestmentPlan? = null

        companion object {

            private var instance: GlobalInvestmentPlanData? = null

            @Synchronized
            fun getInstance(): GlobalInvestmentPlanData {
                if (instance == null) {
                    instance =
                            GlobalInvestmentPlanData()
                }
                return instance as GlobalInvestmentPlanData
            }
        }
    }
