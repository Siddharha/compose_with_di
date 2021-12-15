package com.app.l_pesa.user_device_data.inter

import com.app.l_pesa.user_device_data.models.UserSMSUpdateResponse


interface ICallBackUserSMSUpdate {
    fun onSuccessSMSUpdate(status: UserSMSUpdateResponse.Status)
    fun onErrorSMSUpdate(message: String)
    fun onIncompleteSMSUpdate(jsonMessage: String)
    fun onFailureSMSUpdate(jsonMessage: String)

}