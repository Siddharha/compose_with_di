package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanplan.model.ResLoanTenure

interface ICallBackTermsDescription {

    fun onSelectDescription(s: String)
    fun onSelectTerms(t: ResLoanTenure.Data.Option)
}