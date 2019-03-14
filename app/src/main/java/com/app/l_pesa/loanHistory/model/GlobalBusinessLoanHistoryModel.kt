package com.app.l_pesa.loanHistory.model

class GlobalBusinessLoanHistoryModel

 private constructor()
 {
    var modelData: ResLoanHistoryBusiness.LoanHistory? = null

    companion object {

        private var instance: GlobalBusinessLoanHistoryModel? = null

        @Synchronized
        fun getInstance(): GlobalBusinessLoanHistoryModel {
            if (instance == null) {
                instance =
                        GlobalBusinessLoanHistoryModel()
            }
            return instance as GlobalBusinessLoanHistoryModel
        }
    }

}