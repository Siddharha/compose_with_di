package com.app.l_pesa.dev_options.inter

import com.app.l_pesa.dev_options.models.UserCallLogUpdateResponse

interface ICallBackUserCallLogUpdate {
    fun onSuccessCalLogUpdate(s:UserCallLogUpdateResponse)
    fun onErrorCalLogUpdate(message: String)
    fun onIncompleteCalLogUpdate(jsonMessage: String)
    fun onFailureCalLogUpdate(jsonMessage: String)
}