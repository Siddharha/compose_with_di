package com.app.l_pesa.loanplan.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.loanplan.inter.ICallBackLoanPlans
import com.app.l_pesa.loanplan.model.ResLoan
import com.app.l_pesa.loanplan.presenter.PresenterLoanPlans
import com.google.gson.JsonObject
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

        val view = inflater.inflate(R.layout.fragment_loan_plan_list, container,false)
        return view
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

        Toast.makeText(activity,loanHistory.size.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyLoanPlans() {

    }

    override fun onFailureLoanPlans(jsonMessage: String) {

    }
}