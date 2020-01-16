package com.app.l_pesa.registration.model

import com.app.l_pesa.common.CommonStatusModel



data class ResRegistrationOne(val status: CommonStatusModel, val data: RegistrationData)

data class RegistrationData(val access_token:String,val otp:String,val next:String,val displayName:String)

