package com.app.l_pesa.profile.inter

interface ICallBackPersonalInfo {

    fun onSuccessPersonalInfo()
    fun onSessionTimeOut(message: String)
    fun onFailurePersonalInfo(message: String)
}