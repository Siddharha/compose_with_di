package com.app.l_pesa.loanHistory.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.adapter.CurrentLoanHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackLoanHistory
import com.app.l_pesa.loanHistory.model.ResLoanHistory
import com.app.l_pesa.loanHistory.presenter.PresenterLoanHistory
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.ArrayList


class CurrentLoanHistory:Fragment(), ICallBackLoanHistory {


    private var listLoanHistory                 : ArrayList<ResLoanHistory.LoanHistory>? = null
    private lateinit var adapterLoanHistory     : CurrentLoanHistoryAdapter

    private var hasNext=false
    private var after=""

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
        listLoanHistory      = ArrayList()
        adapterLoanHistory   = CurrentLoanHistoryAdapter(activity!!, listLoanHistory!!,this)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","current_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistory(activity!!,jsonObject,"",this)

        }


    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(com.app.l_pesa.R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            loadHistory()
        }
    }

    override fun onSuccessLoanHistory(loan_history: ArrayList<ResLoanHistory.LoanHistory>, cursors: ResLoanHistory.Cursors, user_credit_score: Int) {

        activity!!.runOnUiThread {
            hasNext =cursors.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            val shared=SharedPref(activity!!)
            shared.userCreditScore=user_credit_score.toString()
            listLoanHistory!!.clear()
            listLoanHistory!!.addAll(loan_history)
            adapterLoanHistory          = CurrentLoanHistoryAdapter(activity!!, listLoanHistory!!,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rvLoan.layoutManager        = llmOBJ
            rvLoan.adapter              = adapterLoanHistory

            adapterLoanHistory.setLoadMoreListener(object : CurrentLoanHistoryAdapter.OnLoadMoreListener {
                override fun onLoadMore() {

                    rvLoan.post {

                        if(hasNext)
                        {
                            loadMore()
                        }

                    }

                }
            })


        }

    }

    override fun onSuccessPaginateLoanHistory(loan_history: ArrayList<ResLoanHistory.LoanHistory>, cursors: ResLoanHistory.Cursors) {

        hasNext =cursors.hasNext
        after   =cursors.after
        if(listLoanHistory!!.size!=0)
        {
            try {

                listLoanHistory!!.removeAt(listLoanHistory!!.size - 1)
                adapterLoanHistory.notifyDataChanged()
                listLoanHistory!!.addAll(loan_history)
                adapterLoanHistory.notifyItemRangeInserted(0, listLoanHistory!!.size)

            }
            catch (e:Exception)
            {}
        }


    }

    override fun onEmptyPaginateLoanHistory() {


    }

    override fun onEmptyLoanHistory() {

        swipeRefreshLayout.isRefreshing = false

    }

    override fun onFailureLoanHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false

    }


    private fun loadMore()
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loanStatusModel  = ResLoanHistory.LoanHistory(0,"",0,"",
                                    "","","",
                                    "","","","","",
                                    "","","","","","","","","")

            listLoanHistory!!.add(loanStatusModel)
            adapterLoanHistory.notifyItemInserted(listLoanHistory!!.size-1)
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","current_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryPaginate(activity!!,jsonObject,after,this)

        }

   }

    override fun onClickList() {
        startActivity(Intent(activity, LoanHistoryDetailsActivity::class.java))
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

}