package com.app.l_pesa.registration.inter

/**
 * Created by Intellij Amiya on 01-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackRegisterOne
{
    fun onSuccessRegistrationOne(access_token: String)
    fun onErrorRegistrationOne(jsonMessage: String)
}
