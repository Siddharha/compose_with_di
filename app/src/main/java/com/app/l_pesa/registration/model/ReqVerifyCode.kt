package com.app.l_pesa.registration.model

data class ReqVerifyCode(
    val email_address: String? = null,
    val otp: String? = null
)