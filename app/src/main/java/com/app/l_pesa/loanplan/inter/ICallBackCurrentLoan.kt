package com.app.l_pesa.loanplan.inter

import com.app.l_pesa.loanplan.model.ResLoanPlans
import java.util.*

/**
 * Created by Intellij Amiya on 21/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackCurrentLoan {

    fun onSuccessLoanPlans(item: ArrayList<ResLoanPlans.Item>, appliedProduct: ResLoanPlans.AppliedProduct?)
    fun onEmptyLoanPlans()
    fun onFailureLoanPlans(jsonMessage: String)
    fun onSessionTimeOut(message: String)
    fun onSuccessLoanPlansDetails(details: ResLoanPlans.Details?)
    fun onSuccessLoanHistory()
}