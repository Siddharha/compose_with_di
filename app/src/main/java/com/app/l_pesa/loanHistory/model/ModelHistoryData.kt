package com.app.l_pesa.loanHistory.model

class ModelHistoryData

    private constructor() {
        var modelData: ResLoanHistory.LoanHistory? = null

        companion object {

            private var instance: ModelHistoryData? = null

            @Synchronized
            fun getInstance(): ModelHistoryData {
                if (instance == null) {
                    instance =
                            ModelHistoryData()
                }
                return instance as ModelHistoryData
            }
        }

}