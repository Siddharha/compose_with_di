package com.app.l_pesa.sms_retrieval

interface OTPReceiveListener {
    fun onOTPReceived(otp: String)
    fun onOTPTimeOut()
}