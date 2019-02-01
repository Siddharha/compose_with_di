package com.app.l_pesa.splash.inter

import com.app.l_pesa.splash.model.ResModelData


/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackCountry {

    fun onSuccessCountry(countries_list: ResModelData)
    fun onEmptyCountry()
    fun onFailureCountry(jsonMessage: String)
}