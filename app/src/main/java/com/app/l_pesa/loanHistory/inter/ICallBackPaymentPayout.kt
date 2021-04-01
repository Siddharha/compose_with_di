package com.app.l_pesa.loanHistory.inter

interface ICallBackPaymentPayout {

    fun onSuccessLoanPayment()
    fun onSessionTimeOut(jsonMessage: String)
    fun onErrorLoanPayment(message: String)
}