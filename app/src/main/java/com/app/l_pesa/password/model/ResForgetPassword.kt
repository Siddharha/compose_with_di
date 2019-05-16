package com.app.l_pesa.password.model


import com.app.l_pesa.common.CommonStatusModel

/**
 * Created by Intellij Amiya on 2/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

data class ResForgetPassword(val status: CommonStatusModel, val data:Data)
data class Data(val type: String)