package com.app.l_pesa.login.inter

interface ICallBackEmail {

    fun onSuccessEmail()
    fun onErrorEmail(errorMessageOBJ: String)
}