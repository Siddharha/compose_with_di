package com.app.l_pesa.investment.inter

import com.app.l_pesa.investment.model.ResInvestmentHistory
import java.util.ArrayList

interface ICallBackInvestmentHistory {

    fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?)
    fun onSuccessInvestmentHistoryPaginate(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?)
    fun onEmptyInvestmentHistory()
    fun onErrorInvestmentHistory(jsonMessage: String)

    fun onSuccessInvestmentWithdrawal()
    fun onErrorInvestmentWithdrawal(message: String)
}