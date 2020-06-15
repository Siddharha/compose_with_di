package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.Data
import com.app.l_pesa.registration.model.RegistrationData
import org.json.JSONObject


interface ICallBackEmailVerify
{
    fun onSuccessEmailVerify(data: Data)
    fun onErrorEmailVerify(jsonMessage: String)
    fun onErrorEmailCode(code: JSONObject)
}
