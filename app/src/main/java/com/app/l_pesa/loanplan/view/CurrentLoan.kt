package com.app.l_pesa.loanplan.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.loanplan.adapter.CurrentLoanPlanAdapter
import com.app.l_pesa.loanplan.inter.ICallBackLoanPlans
import com.app.l_pesa.loanplan.model.ResLoan
import com.app.l_pesa.loanplan.presenter.PresenterLoanPlans
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.ArrayList

/**
 * Created by Intellij Amiya on 21/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class CurrentLoan:Fragment(), ICallBackLoanPlans {


    companion object {
        fun newInstance(): Fragment {
            return CurrentLoan()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loanLoan()
    }

    private fun loanLoan()
    {
        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type","current_loan")
        val presenterLoanPlans= PresenterLoanPlans()
        presenterLoanPlans.getLoanPlans(activity!!,jsonObject,this)
    }

    override fun onSuccessLoanPlans(loanHistory: ArrayList<ResLoan.LoanHistory>) {

       val currentLoanAdapter    = CurrentLoanPlanAdapter(activity!!, loanHistory)
        rvLoan.layoutManager     = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter           = currentLoanAdapter
    }

    override fun onEmptyLoanPlans() {

    }

    override fun onFailureLoanPlans(jsonMessage: String) {

    }
}