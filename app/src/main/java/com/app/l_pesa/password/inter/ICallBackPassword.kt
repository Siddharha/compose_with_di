package com.app.l_pesa.password.inter

/**
 * Created by Intellij Amiya on 2/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackPassword {

    fun onSuccessResetPassword(message: String)
    fun onErrorResetPassword(jsonMessage: String)
}