package com.app.l_pesa.pin.inter

import com.app.l_pesa.pin.model.PinData

interface ICallBackChangePin {

    fun onSuccessResetPin(data: PinData)
    fun onErrorResetPin(message: String)
}