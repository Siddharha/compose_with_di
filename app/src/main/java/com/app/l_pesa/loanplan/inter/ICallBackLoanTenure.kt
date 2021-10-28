package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanplan.model.ResLoanTenure


interface ICallBackLoanTenure {

    fun onSuccessLoanTenureList(item: List<ResLoanTenure.Data.Option>)
    fun onEmptyLoanTenureList()
    fun onFailureLoanTenureList(jsonMessage: String)
    fun onSessionTimeOut(message: String)
}