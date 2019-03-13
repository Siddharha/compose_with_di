package com.app.l_pesa.profile.inter

interface ICallBackUpload {

    fun onSuccessUploadAWS(url: String)
    fun onFailureUploadAWS(string: String)
}