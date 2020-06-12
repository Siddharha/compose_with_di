package com.app.l_pesa.registration.model

import com.app.l_pesa.common.CommonStatusModel

data class EmailVerifyResponse(
    val status: CommonStatusModel? = null,
    val data: Data? = null
)

data class Data(
    val next: String? = null
)