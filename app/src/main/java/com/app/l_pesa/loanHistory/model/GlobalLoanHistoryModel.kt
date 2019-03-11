package com.app.l_pesa.loanHistory.model

class GlobalLoanHistoryModel

    private constructor() {
        var modelData: ResLoanHistory.LoanHistory? = null

        companion object {

            private var instance: GlobalLoanHistoryModel? = null

            @Synchronized
            fun getInstance(): GlobalLoanHistoryModel {
                if (instance == null) {
                    instance =
                            GlobalLoanHistoryModel()
                }
                return instance as GlobalLoanHistoryModel
            }
        }

}