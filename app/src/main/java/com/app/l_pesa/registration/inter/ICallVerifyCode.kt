package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.Data

interface ICallVerifyCode {
    fun onVerificationSuccess(data: Data)
    fun onVerifyFailure(msg: String)
}