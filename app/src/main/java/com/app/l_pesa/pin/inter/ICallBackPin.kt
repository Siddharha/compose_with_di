package com.app.l_pesa.pin.inter

interface ICallBackPin {
    fun onSuccessChangePin()
    fun onFailureChangePin(message: String)
    fun onSessionTimeOut(message: String)
}