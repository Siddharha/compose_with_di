package com.app.l_pesa.profile.inter

interface ICallBackStatementDelete {

    fun onSuccessStatementDelete()
    fun onFailureStatementDelete(string: String)

    fun onDeleteTimeOut(string:String)
}