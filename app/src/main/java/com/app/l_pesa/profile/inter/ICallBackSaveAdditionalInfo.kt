package com.app.l_pesa.profile.inter

interface ICallBackSaveAdditionalInfo {
    fun onSessionTimeOut(jsonMessage: String)
    fun onFailureSaveAdditionalInfo(jsonMessage: String)
    fun onSucessSaveAdditionalInfo(message: String)
}