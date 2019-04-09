package com.app.l_pesa.investment.inter

import android.widget.ImageButton
import com.app.l_pesa.investment.model.ResInvestmentHistory

interface ICallBackEditHistory {

    fun onEditWindow(imgEdit: ImageButton, btnWithdrawalShow: ResInvestmentHistory.ActionState)
}