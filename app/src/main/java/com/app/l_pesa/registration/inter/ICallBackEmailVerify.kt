package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.Data
import com.app.l_pesa.registration.model.RegistrationData


interface ICallBackEmailVerify
{
    fun onSuccessEmailVerify(data: Data)
    fun onErrorEmailVerify(jsonMessage: String)
}
