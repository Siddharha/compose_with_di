package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanplan.model.ResPaybackSchedule

interface ICallBackPaybackSchedule {

    fun onSuccessPaybackSchedule(data: ResPaybackSchedule.Data)
    fun onErrorPaybackSchedule(jsonMessage: String)
}