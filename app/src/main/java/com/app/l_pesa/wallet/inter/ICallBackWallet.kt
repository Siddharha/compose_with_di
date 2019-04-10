package com.app.l_pesa.wallet.inter

interface ICallBackWallet {

    fun onSuccessWalletWithdrawal()
    fun onErrorWalletWithdrawal(message: String)
}