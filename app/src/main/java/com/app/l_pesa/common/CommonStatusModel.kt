package com.app.l_pesa.common

import android.support.annotation.Keep


/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

@Keep
data class CommonStatusModel(val statusCode: String, val isSuccess: Boolean, val message: String)