package com.app.l_pesa.loanplan.model

class GlobalLoanPlanModel

    private constructor() {
        var modelData: ResLoanPlans.Details? = null

        companion object {

            private var instance: GlobalLoanPlanModel? = null

            @Synchronized
            fun getInstance(): GlobalLoanPlanModel {
                if (instance == null) {
                    instance =
                            GlobalLoanPlanModel()
                }
                return instance as GlobalLoanPlanModel
            }
        }
}