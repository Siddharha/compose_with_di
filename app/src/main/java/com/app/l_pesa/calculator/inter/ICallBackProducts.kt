package com.app.l_pesa.calculator.inter

import com.app.l_pesa.calculator.model.ResProducts

interface ICallBackProducts {

    fun onSuccessCurrentLoan(data: ResProducts.Data)
    fun onEmptyCurrentLoan()
    fun onErrorCurrentLoan(errorMessageOBJ: String)

    fun onSuccessBusinessLoan(data: ResProducts.Data)
    fun onEmptyBusinessLoan()
    fun onErrorBusinessLoan(errorMessageOBJ: String)

    fun onSessionTimeOut(jsonMessage: String)
}