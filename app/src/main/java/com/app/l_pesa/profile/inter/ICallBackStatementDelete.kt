package com.app.l_pesa.profile.inter

interface ICallBackStatementDelete {

    fun onSuccessStatementDelete()
    fun onFailureStatementDelete(string: String)
    fun isFailureStatementDelete(bool: Boolean)
    fun onDeleteTimeOut(string:String)
}