package com.app.l_pesa.investment.inter

import android.view.View
import com.app.l_pesa.investment.model.ResInvestmentHistory

interface ICallBackPopUpWindow
{
    fun onItemClick(view: View, position: Int, modelWindowHistory: String, investmentList: ResInvestmentHistory.UserInvestment)
}