package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.RegistrationData


interface ICallBackRegisterOne
{
    fun onSuccessRegistrationOne(data: RegistrationData)
    fun onErrorRegistrationOne(jsonMessage: String)
}
