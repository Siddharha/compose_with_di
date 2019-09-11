package com.app.l_pesa.login.inter

interface ICallBackResend {

    fun onSuccessResend()
    fun onErrorResend(errorMessageOBJ: String)
}