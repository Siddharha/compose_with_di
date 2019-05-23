package com.app.l_pesa.pinview.inter

import com.app.l_pesa.pinview.model.LoginData


interface ICallBackPinSet {

    fun onSuccessPinSet(data: LoginData)
    fun onErrorPinSet(message: String)
}