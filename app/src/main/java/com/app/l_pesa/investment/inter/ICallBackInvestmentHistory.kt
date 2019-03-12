package com.app.l_pesa.investment.inter

import com.app.l_pesa.investment.model.ResInvestmentHistory
import java.util.ArrayList

interface ICallBackInvestmentHistory {

    fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>)
    fun onEmptyInvestmentHistory()
    fun onErrorInvestmentHistory(jsonMessage: String)
}