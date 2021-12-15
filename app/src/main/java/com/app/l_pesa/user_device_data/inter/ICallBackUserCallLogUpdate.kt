package com.app.l_pesa.user_device_data.inter

import com.app.l_pesa.user_device_data.models.UserCallLogUpdateResponse

interface ICallBackUserCallLogUpdate {
    fun onSuccessCalLogUpdate(s:UserCallLogUpdateResponse)
    fun onErrorCalLogUpdate(message: String)
    fun onIncompleteCalLogUpdate(jsonMessage: String)
    fun onFailureCalLogUpdate(jsonMessage: String)
}