package com.app.l_pesa.registration.inter

import com.app.l_pesa.api.Result

interface EmailVerifyListener {
    fun onEmailVerifyResponse(result: Result<Any>)
}