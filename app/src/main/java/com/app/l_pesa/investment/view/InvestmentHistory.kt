package com.app.l_pesa.investment.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.investment.adapter.InvestmentHistoryAdapter
import com.app.l_pesa.investment.inter.ICallBackInvestmentHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import com.app.l_pesa.investment.presenter.PresenterInvestmentHistory
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.ArrayList

class InvestmentHistory:Fragment(),ICallBackInvestmentHistory {


    companion object {
        fun newInstance(): Fragment {
            return InvestmentHistory()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initUI()

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            initUI()
        }
    }

    private fun initUI()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistory(activity!!,this)
        }
    }

    override fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>) {

        swipeRefreshLayout.isRefreshing    = false
        val investmentHistoryAdapter       = InvestmentHistoryAdapter(activity!!, userInvestment)
        rvLoan.layoutManager               = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter                     = investmentHistoryAdapter
    }

    override fun onEmptyInvestmentHistory() {

        Toast.makeText(activity,"EMPTY", Toast.LENGTH_SHORT).show()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorInvestmentHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity,""+jsonMessage,Toast.LENGTH_SHORT).show()
    }
}