package com.app.l_pesa.registration.inter

import com.app.l_pesa.API.Result

interface EmailVerifyListener {
    fun onEmailVerifyResponse(result: Result<Any>)
}