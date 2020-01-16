package com.app.l_pesa.splash.inter

import com.app.l_pesa.splash.model.ResModelData

interface ICallBackCountry {

    fun onSuccessCountry(countries_list: ResModelData)
    fun onEmptyCountry()
    fun onFailureCountry(jsonMessage: String)
}