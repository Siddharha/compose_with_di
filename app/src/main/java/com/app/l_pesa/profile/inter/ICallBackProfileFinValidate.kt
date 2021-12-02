package com.app.l_pesa.profile.inter

interface ICallBackProfileFinValidate {
    fun onSessionTimeOut(jsonMessage: String)
    fun onFailureHasBusiness(jsonMessage: String)
    fun onSuccessHasBusiness(jsonMessage: String)

    fun onFailureIsEmp(jsonMessage: String)
    fun onSuccessIsEmp(jsonMessage: String)

}