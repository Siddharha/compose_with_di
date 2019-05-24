package com.app.l_pesa.password.inter

import com.app.l_pesa.password.model.PinData

interface ICallBackPassword {

    fun onSuccessResetPassword(data: PinData)
    fun onErrorResetPassword(message: String)
}