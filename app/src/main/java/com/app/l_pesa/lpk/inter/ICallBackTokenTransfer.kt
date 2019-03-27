package com.app.l_pesa.lpk.inter

interface ICallBackTokenTransfer {

    fun onSuccessTokenTransfer()
    fun onErrorTokenTransfer(message: String)
}