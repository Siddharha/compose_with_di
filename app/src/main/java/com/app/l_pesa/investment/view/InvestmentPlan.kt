package com.app.l_pesa.investment.view


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.investment.adapter.InvestmentPlanAdapter
import com.app.l_pesa.investment.inter.ICallBackInvestmentPlan
import com.app.l_pesa.investment.model.ResInvestmentPlan
import com.app.l_pesa.investment.presenter.PresenterInvestmentPlan
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.text.DecimalFormat


class InvestmentPlan:Fragment(), ICallBackInvestmentPlan {


    companion object {
        fun newInstance(): Fragment {
            return InvestmentPlan()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        investmentPlan()
        swipeRefresh()

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            investmentPlan()
        }
    }

    private fun investmentPlan()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterLoanPlans= PresenterInvestmentPlan()
            presenterLoanPlans.getInvestmentPlan(activity!!,this)
        }

    }

    override fun onSuccessInvestmentPlan(data: ResInvestmentPlan.Data) {

        val sharedPrefOBJ= SharedPref(activity!!)

        val gSonData = Gson()
        val json = gSonData.toJson(data)
        sharedPrefOBJ.loanPlanList  =json

        swipeRefreshLayout.isRefreshing    = false
        val investmentPlanAdapter          = InvestmentPlanAdapter(activity!!, data.investmentPlans!!,this)
        rvLoan.layoutManager               = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter                     = investmentPlanAdapter

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        sharedPrefOBJ.investRateMin=format.format(data.investmentPlans!![0].depositInterestRate)
        sharedPrefOBJ.investRateMax=format.format(data.investmentPlans!![data.investmentPlans!!.size-1].depositInterestRate)
    }

    override fun onEmptyInvestmentPlan() {

       swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorInvestmentPlan(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity,""+jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onClickInvestmentPlan(investmentPlan: ResInvestmentPlan.InvestmentPlan) {

        val globalInvestmentPlan= GlobalInvestmentPlanData.getInstance()
        globalInvestmentPlan.modelData=investmentPlan

        startActivity(Intent(activity, InvestmentApplyActivity::class.java))
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

}