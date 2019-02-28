package com.app.l_pesa.loanHistory.inter

import com.app.l_pesa.loanHistory.model.ResPaybackSchedule

interface ICallBackPaybackSchedule {

    fun onSuccessPaybackSchedule(data: ResPaybackSchedule.Data)
    fun onErrorPaybackSchedule(jsonMessage: String)
}