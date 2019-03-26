package com.app.l_pesa.pin.inter

interface ICallBackPin {
    fun onSuccessChangePin()
    fun onFailureChangePin(message: String)
}