package com.app.l_pesa.login.inter

import com.app.l_pesa.login.model.LoginData


/**
 * Created by Intellij Amiya on 28-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackLogin {

    fun onSuccessLogin(data: LoginData)
    fun onIncompleteLogin()
    fun onErrorLogin(jsonMessage: String)
    fun onFailureLogin(jsonMessage: String)
}