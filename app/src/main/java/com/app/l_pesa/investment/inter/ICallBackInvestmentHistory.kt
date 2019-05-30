package com.app.l_pesa.investment.inter

import com.app.l_pesa.investment.model.ResInvestmentHistory
import java.util.ArrayList

interface ICallBackInvestmentHistory {

    fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?, from_date: String, to_date: String)
    fun onSuccessInvestmentHistoryPaginate(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?, from_date: String, to_date: String)
    fun onEmptyInvestmentHistory(type: String)
    fun onErrorInvestmentHistory(jsonMessage: String)

    fun onSuccessInvestmentWithdrawal()
    fun onErrorInvestmentWithdrawal(message: String)

    fun onSessionTimeOut(message: String)

    fun onSuccessReinvestment()
    fun onErrorReinvestment(message: String)

    fun onSuccessExitPoint()
    fun onErrorExitPoint(message: String)

    fun onSuccessRemoveInvestment(position:Int)
    fun onErrorRemoveInvestment(message: String)
}