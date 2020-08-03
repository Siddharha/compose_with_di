package com.app.l_pesa.pin.model

import com.app.l_pesa.common.CommonStatusModel

data class ResForgotSms(
        val status: CommonStatusModel,
        val data: Data
)

data class Data(
        val next_step: String
)