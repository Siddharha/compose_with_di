package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanplan.model.LoanTermItem

interface ICallBackTermsDescription {

    fun onSelectDescription(s: String)
    fun onSelectTerms(t: LoanTermItem)
}