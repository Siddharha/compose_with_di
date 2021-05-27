package com.app.l_pesa.dev_options.inter

import com.app.l_pesa.dev_options.models.UserSMSUpdateResponse


interface ICallBackUserSMSUpdate {
    fun onSuccessSMSUpdate(status: UserSMSUpdateResponse.Status)
    fun onErrorSMSUpdate(message: String)
    fun onIncompleteSMSUpdate(jsonMessage: String)
    fun onFailureSMSUpdate(jsonMessage: String)

}