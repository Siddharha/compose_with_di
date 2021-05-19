package com.app.l_pesa.allservices.inter

import com.app.l_pesa.allservices.models.SasaUserInfoResponse
import com.app.l_pesa.profile.model.ResUserInfo

interface ICallBackSasaUser {
    fun onSuccessUserInfo(data: SasaUserInfoResponse.Data)
    fun onSessionTimeOut(jsonMessage: String)
    fun onErrorUserInfo(jsonMessage: String)
}