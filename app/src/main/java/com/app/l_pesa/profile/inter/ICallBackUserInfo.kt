package com.app.l_pesa.profile.inter

import com.app.l_pesa.profile.model.ResUserInfo

interface ICallBackUserInfo {

    fun onSuccessUserInfo(data: ResUserInfo.Data)
    fun onErrorUserInfo(message: String)
}