package com.app.l_pesa.profile.inter

interface ICallBackBusinessInfo {

    fun onSuccessBusinessInfo()
    fun onFailureBusinessInfo(message: String)
    fun onSessionTimeOut(message: String)
}