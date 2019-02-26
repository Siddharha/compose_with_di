package com.app.l_pesa.loanHistory.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.loanHistory.adapter.CurrentLoanHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackLoanHistory
import com.app.l_pesa.loanHistory.model.ResLoanHistory
import com.app.l_pesa.loanHistory.presenter.PresenterLoanHistory
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.ArrayList

class CurrentLoanHistory:Fragment(), ICallBackLoanHistory {


    companion object {
        fun newInstance(): Fragment {
            return CurrentLoanHistory()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadHistory()
        swipeRefresh()

    }

    private fun loadHistory()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","current_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistory(activity!!,jsonObject,this)
        }

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            loadHistory()
        }
    }

    override fun onSuccessLoanHistory(loanHistory: ArrayList<ResLoanHistory.LoanHistory>) {

        swipeRefreshLayout.isRefreshing = false
        val currentLoanAdapter          = CurrentLoanHistoryAdapter(activity!!, loanHistory)
        rvLoan.layoutManager            = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter                  = currentLoanAdapter
    }

    override fun onEmptyLoanHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onFailureLoanHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
    }
}