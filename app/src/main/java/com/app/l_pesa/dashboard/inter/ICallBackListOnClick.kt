package com.app.l_pesa.dashboard.inter

interface ICallBackListOnClick {

    fun onClickLoanList(type: String)
    fun onClickPay(type: String, loan_id: String)
}