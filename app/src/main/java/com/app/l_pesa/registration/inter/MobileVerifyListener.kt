package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.NextStage

interface MobileVerifyListener {

    fun onResponseVerifyMobile(data: NextStage)
    fun onFailure(msg: String)
}