package com.app.l_pesa.loanplan.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.loanplan.adapter.BusinessLoanPlanAdapter
import com.app.l_pesa.loanplan.inter.ICallBackLoanPlans
import com.app.l_pesa.loanplan.model.ResLoanHistory
import com.app.l_pesa.loanplan.model.ResLoanPlans
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
class BusinessLoan:Fragment(), ICallBackLoanPlans {

    companion object {
        fun newInstance(): Fragment {
            return BusinessLoan()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loanLoan()
    }

    private fun loanLoan()
    {
        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type","business_loan")
        val presenterLoanPlans= PresenterLoanPlans()
        presenterLoanPlans.getLoanPlans(activity!!,jsonObject,this)
    }

    override fun onSuccessLoanPlans(item: ArrayList<ResLoanPlans.Item>) {

        val businessLoanAdapter  = BusinessLoanPlanAdapter(activity!!, item)
        rvLoan.layoutManager     = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter           = businessLoanAdapter
    }

    override fun onEmptyLoanPlans() {

        Toast.makeText(activity,"",Toast.LENGTH_SHORT).show()
    }

    override fun onFailureLoanPlans(jsonMessage: String) {

        Toast.makeText(activity,jsonMessage,Toast.LENGTH_SHORT).show()
    }
}