package com.app.l_pesa.profile.inter

import com.app.l_pesa.profile.model.ResUserAdditionalInfoDropdowns

interface ICallBackProfileAdditionalInfo {
    fun onSessionTimeOut(jsonMessage: String)
    fun onFailureAdditionalInfo(jsonMessage: String)
    fun onSuccessAdditionalInfo(data: ResUserAdditionalInfoDropdowns.Data)
}