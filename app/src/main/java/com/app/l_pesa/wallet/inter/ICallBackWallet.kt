package com.app.l_pesa.wallet.inter

interface ICallBackWallet {

    fun onSuccessWalletWithdrawal(message: String)
    fun onErrorWalletWithdrawal(message: String)
}