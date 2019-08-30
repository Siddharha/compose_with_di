package com.app.l_pesa.registration.model

import com.app.l_pesa.common.CommonStatusModel


/**
 * Created by Intellij Amiya on 29-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

data class ResRegistrationOne(val status: CommonStatusModel, val data: RegistrationData)

data class RegistrationData(val access_token:String,val otp:String,val next:String,val displayName:String)

