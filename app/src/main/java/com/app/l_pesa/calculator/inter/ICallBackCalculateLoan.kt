package com.app.l_pesa.calculator.inter

import com.app.l_pesa.calculator.model.ResCalculation
import com.app.l_pesa.calculator.model.ResProducts

interface ICallBackCalculateLoan {

    fun onSuccessCalculateLoan(data: ResCalculation.Data)
    fun onErrorCalculateLoan(errorMessageOBJ: String)

    fun onSessionTimeOut(jsonMessage: String)

    //fun onClickProduct(productList: ResProducts.ProductList, product: ResProducts.Data)
}