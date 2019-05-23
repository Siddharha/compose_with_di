package com.app.l_pesa.loanHistory.inter

interface ICallBackLoanApply {

    fun onSuccessLoanApply()
    fun onSessionTimeOut(jsonMessage: String)
    fun onErrorLoanApply(message: String)
}