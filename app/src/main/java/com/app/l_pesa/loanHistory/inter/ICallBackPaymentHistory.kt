package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResPaymentHistory
import java.util.ArrayList

interface ICallBackPaymentHistory {

    fun onSuccessPaymentHistory(paymentHistory: ArrayList<ResPaymentHistory.PaymentHistory>)
    fun onEmptyPaymentHistory()
    fun onErrorPaymentHistory(message: String)
}