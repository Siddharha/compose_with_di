package com.app.l_pesa.dev_options.inter

interface ICallBackUserCallLogUpdate {
    fun onSuccessCalLogUpdate(s:Any)
    fun onErrorCalLogUpdate(message: String)
    fun onIncompleteCalLogUpdate(jsonMessage: String)
    fun onFailureCalLogUpdate(jsonMessage: String)
}