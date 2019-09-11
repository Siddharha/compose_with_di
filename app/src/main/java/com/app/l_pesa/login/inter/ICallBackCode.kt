package com.app.l_pesa.login.inter

interface ICallBackCode {

    fun onSuccessVerification()
    fun onErrorVerification(errorMessageOBJ: String)
}