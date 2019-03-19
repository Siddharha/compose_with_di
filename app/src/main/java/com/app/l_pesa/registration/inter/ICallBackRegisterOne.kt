package com.app.l_pesa.registration.inter

import com.app.l_pesa.registration.model.RegistrationData

/**
 * Created by Intellij Amiya on 01-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackRegisterOne
{
    fun onSuccessRegistrationOne(data: RegistrationData)
    fun onErrorRegistrationOne(jsonMessage: String)
}
