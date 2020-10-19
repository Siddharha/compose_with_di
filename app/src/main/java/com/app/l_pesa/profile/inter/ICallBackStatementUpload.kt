package com.app.l_pesa.profile.inter

interface ICallBackStatementUpload {

    fun onSuccessUploadAWS(url: String)
    fun onFailureUploadAWS(string: String)
    fun onProgressUploadAWS(progress: Int)

    fun onSucessUploadStatement()
    fun onFailureUploadStatement(string: String)

    fun onUploadTimeOut(string:String)
}