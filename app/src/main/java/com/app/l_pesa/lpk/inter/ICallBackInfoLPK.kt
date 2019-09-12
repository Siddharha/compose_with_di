package com.app.l_pesa.lpk.inter

import com.app.l_pesa.lpk.model.ResInfoLPK

interface ICallBackInfoLPK {

    fun onSuccessInfoLPK(data: ResInfoLPK.Data?, type: String)
    fun onErrorInfoLPK(message: String)
    fun onSessionTimeOut(jsonMessage: String)
}