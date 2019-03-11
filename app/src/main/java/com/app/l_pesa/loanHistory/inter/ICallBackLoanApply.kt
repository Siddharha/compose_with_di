package com.app.l_pesa.loanHistory.inter

interface ICallBackLoanApply {

    fun onSuccessLoanApply()
    fun onErrorLoanApply(message: String)
}