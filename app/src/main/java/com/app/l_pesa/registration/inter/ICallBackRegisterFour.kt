package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.RegisterPageIdListResp

interface ICallBackRegisterFour {

    fun onSuccessIdListResp(list : List<RegisterPageIdListResp.Data.IdType>)
    fun onErrorIdListResp(jsonMessage: String)
}