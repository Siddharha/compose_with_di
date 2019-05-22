package com.app.l_pesa.login.inter

import com.app.l_pesa.login.model.PinData


/**
 * Created by Intellij Amiya on 28-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackLogin {

    fun onSuccessLogin(data: PinData)
    fun onIncompleteLogin(message: String)
    fun onErrorLogin(jsonMessage: String)
    fun onFailureLogin(jsonMessage: String)
}