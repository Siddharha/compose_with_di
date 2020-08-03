package com.app.l_pesa.pin.inter

import com.app.l_pesa.pin.model.Data
import com.app.l_pesa.pin.model.PinData

interface ICallBackSms {

    fun onSuccessSms(data: Data)
    fun onErrorSms(message: String)
}