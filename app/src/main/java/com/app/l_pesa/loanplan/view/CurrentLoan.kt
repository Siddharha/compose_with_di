package com.app.l_pesa.loanplan.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.view.LoanHistoryDetailsActivity
import com.app.l_pesa.loanplan.adapter.CurrentLoanPlanAdapter
import com.app.l_pesa.loanplan.inter.ICallBackCurrentLoan
import com.app.l_pesa.loanplan.model.GlobalLoanPlanModel
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
class CurrentLoan:Fragment(), ICallBackCurrentLoan {


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

        loadLoan()
        swipeRefresh()
    }

    private fun loadLoan()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","current_loan")
            val presenterLoanPlans= PresenterLoanPlans()
            presenterLoanPlans.doLoanPlans(activity!!,jsonObject,this)
        }

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

           loadLoan()
        }
    }

    override fun onSuccessLoanPlans(item: ArrayList<ResLoanPlans.Item>, appliedProduct: ResLoanPlans.AppliedProduct?) {

        (activity as DashboardActivity).isVisibleToolbarRight(true)
        cardView.visibility   = View.GONE
        rvLoan.visibility     = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
        val currentLoanAdapter = CurrentLoanPlanAdapter(activity!!, item, appliedProduct!!, this)
        rvLoan.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter = currentLoanAdapter
    }

    override fun onEmptyLoanPlans() {

        (activity as DashboardActivity).isVisibleToolbarRight(false)
        rvLoan.visibility   = View.GONE
        cardView.visibility = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false

    }

    override fun onFailureLoanPlans(jsonMessage: String) {

        (activity as DashboardActivity).isVisibleToolbarRight(false)
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onSuccessLoanPlansDetails(details: ResLoanPlans.Details?) {

        val globalLoanPlanModel= GlobalLoanPlanModel.getInstance()
        globalLoanPlanModel.modelData=details
        startActivity(Intent(activity, LoanPlanDetailsActivity::class.java))
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onSuccessLoanHistory() {
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","current_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }
}