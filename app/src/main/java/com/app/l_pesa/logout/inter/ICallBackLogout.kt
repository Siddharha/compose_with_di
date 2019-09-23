package com.app.l_pesa.logout.inter

interface ICallBackLogout {

    fun onSuccessLogout()
    fun onSessionTimeOut()
    fun onErrorLogout(message: String)
}