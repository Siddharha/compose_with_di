package com.app.l_pesa.pin.inter


interface ICallBackLoginPin {

    fun onSuccessResetPin(message: String)
    fun onErrorResetPin(jsonMessage: String)
    fun onSessionTimeOut(message: String)
}