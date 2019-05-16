package com.app.l_pesa.settings.inter

interface ICallBackResetPassword {

    fun onSuccessResetPassword(message: String)
    fun onErrorResetPassword(jsonMessage: String)
}