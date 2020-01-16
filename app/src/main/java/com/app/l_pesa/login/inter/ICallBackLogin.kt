package com.app.l_pesa.login.inter

import com.app.l_pesa.login.model.PinData

interface ICallBackLogin {

    fun onSuccessLogin(data: PinData)
    fun onIncompleteLogin(message: String)
    fun onErrorLogin(jsonMessage: String)
    fun onFailureLogin(jsonMessage: String)
}