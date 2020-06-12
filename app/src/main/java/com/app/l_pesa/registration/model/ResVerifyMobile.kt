package com.app.l_pesa.registration.model

import com.app.l_pesa.common.CommonStatusModel

data class ResVerifyMobile(
    val status: CommonStatusModel? = null,
    val data: NextStage? = null
)

data class NextStage(
    val next: String? = null,
    val user: User? = null,
    val access_token: String? = null
)

data class User(
    val name: String? = null,
    val profile_image: String? = null
)