package com.app.l_pesa.loanHistory.model

class GlobalCurrentLoanHistoryModel

    private constructor() {
        var modelData: ResLoanHistoryCurrent.LoanHistory? = null

        companion object {

            private var instance: GlobalCurrentLoanHistoryModel? = null

            @Synchronized
            fun getInstance(): GlobalCurrentLoanHistoryModel {
                if (instance == null) {
                    instance =
                            GlobalCurrentLoanHistoryModel()
                }
                return instance as GlobalCurrentLoanHistoryModel
            }
        }

}