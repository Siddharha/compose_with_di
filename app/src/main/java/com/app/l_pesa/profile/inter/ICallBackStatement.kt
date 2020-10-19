package com.app.l_pesa.profile.inter

import com.app.l_pesa.profile.model.statement.StatementListResponse
import com.app.l_pesa.profile.model.statement.StatementTypeResponse

interface ICallBackStatement {

    fun onSuccessGetStatementType(statementTypes : List<StatementTypeResponse.Data.StatementType>)
    fun onFailureGetStatementType(message: String)

    fun onSuccessGetStatementList(statementList : List<StatementListResponse.Data>)
    fun onFailureGetStatementList(message: String)
//    fun onSuccessDeleteProof(position: Int)
//    fun onFailureDeleteProof(message: String)
    fun onSessionTimeOut(message: String)
}