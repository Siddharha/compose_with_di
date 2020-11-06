package com.app.l_pesa.profile.zoop

interface ICallBackZoop {
    fun onSucessInit(response: ZoopInitResponse)
    fun onFailureInit(response: ZoopInitFailureResponse)
    fun onUnknownErr(mgs:String)
}