package com.app.l_pesa.splash.inter

interface ICallBackVersion {
    fun onResponse(status: Boolean)
    fun onFailureCountry(jsonMessage: String)
}