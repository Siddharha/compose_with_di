package com.app.l_pesa.help.inter

import com.app.l_pesa.help.model.HelpData

interface ICallBackHelp {

    fun onSuccessHelp(data: HelpData)
    fun onErrorHelp(message: String)
    fun onSessionTimeOut(message: String)
}