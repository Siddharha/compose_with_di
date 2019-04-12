package com.app.l_pesa.lpk.inter

interface ICallBackInvestmentStatus {

    fun onSuccessInvestmentStatus()
    fun onErrorInvestmentStatus(message: String)
}