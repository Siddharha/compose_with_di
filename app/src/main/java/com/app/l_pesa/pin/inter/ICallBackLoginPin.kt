package com.app.l_pesa.pin.inter

/**
 * Created by Intellij Amiya on 2/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackLoginPin {

    fun onSuccessResetPin(message: String)
    fun onErrorResetPin(jsonMessage: String)
}