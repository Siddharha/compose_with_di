package com.app.l_pesa.otpview.inter

import com.app.l_pesa.otpview.model.PinData

interface ICallBackVerifyOTP {

    fun onSuccessVerifyOTP(data: PinData)
    fun onErrorVerifyOTP(message: String)

    fun onSuccessResendOTP()
    fun onErrorResendOTP(message: String)
}