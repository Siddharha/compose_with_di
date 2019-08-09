package com.app.l_pesa.lpk.inter

interface ICallBackWalletAddress {

    fun onSuccessWalletAddress()
    fun onErrorWalletAddress(message: String)
    fun onSessionTimeOut(message: String)
}