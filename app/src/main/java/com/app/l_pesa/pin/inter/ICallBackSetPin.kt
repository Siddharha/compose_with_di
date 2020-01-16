package com.app.l_pesa.pin.inter

interface ICallBackSetPin {

    fun onSuccessSetPin()
    fun onFailureSetPin(message: String)
    fun onSessionTimeOut(message: String)
}