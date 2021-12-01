package com.app.l_pesa.profile.inter

interface ICallBackProfileBusinessValidate {
    fun onSessionTimeOut(jsonMessage: String)
    fun onFailureHasBusiness(jsonMessage: String)
    fun onSuccessHasBusiness()

}