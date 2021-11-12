package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResLoanPayment

interface ICallBackPaymentPayout {

    fun onSuccessLoanPayment(loanPaymentData:ResLoanPayment.Data)
    fun onSessionTimeOut(jsonMessage: String)
    fun onErrorLoanPayment(message: String)
}