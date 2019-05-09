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
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.adapter.CurrentLoanHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackCurrentLoanHistory
import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import com.app.l_pesa.loanHistory.presenter.PresenterLoanHistory
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_history_list.*
import java.util.ArrayList


class CurrentLoanHistory:Fragment(), ICallBackCurrentLoanHistory {


    private var listLoanHistoryCurrent          : ArrayList<ResLoanHistoryCurrent.LoanHistory>? = null
    private lateinit var adapterLoanHistory     : CurrentLoanHistoryAdapter

    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return CurrentLoanHistory()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_history_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadHistory()
        swipeRefresh()

        buttonApplyLoan.setOnClickListener {

            val sharedPref = SharedPref(activity!!)
            sharedPref.navigationTab = resources.getString(R.string.open_tab_loan)
            sharedPref.openTabLoan = "CURRENT"
            val intent = Intent(activity, DashboardActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

    }

    private fun loadHistory()
    {
        listLoanHistoryCurrent      = ArrayList()
        adapterLoanHistory   = CurrentLoanHistoryAdapter(activity!!, listLoanHistoryCurrent!!,this)
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

    fun doFilter()
    {

    }

    override fun onSuccessLoanHistory(loan_historyCurrent: ArrayList<ResLoanHistoryCurrent.LoanHistory>, cursors: ResLoanHistoryCurrent.Cursors, user_credit_score: Int) {

        cardView.visibility  = View.GONE
        rvLoan.visibility    = View.VISIBLE

        activity!!.runOnUiThread {
            hasNext =cursors.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            val shared=SharedPref(activity!!)
            shared.userCreditScore=user_credit_score.toString()
            listLoanHistoryCurrent!!.clear()
            listLoanHistoryCurrent!!.addAll(loan_historyCurrent)
            adapterLoanHistory          = CurrentLoanHistoryAdapter(activity!!, listLoanHistoryCurrent!!,this)
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

    override fun onSuccessPaginateLoanHistory(loan_historyCurrent: ArrayList<ResLoanHistoryCurrent.LoanHistory>, cursors: ResLoanHistoryCurrent.Cursors) {

        hasNext =cursors.hasNext
        after   =cursors.after
        if(listLoanHistoryCurrent!!.size!=0)
        {
            try {

                listLoanHistoryCurrent!!.removeAt(listLoanHistoryCurrent!!.size - 1)
                adapterLoanHistory.notifyDataChanged()
                listLoanHistoryCurrent!!.addAll(loan_historyCurrent)
                adapterLoanHistory.notifyItemRangeInserted(0, listLoanHistoryCurrent!!.size)

            }
            catch (e:Exception)
            {}
        }


    }


    override fun onEmptyLoanHistory() {

        swipeRefreshLayout.isRefreshing = false
        rvLoan.visibility  =View.GONE
        cardView.visibility=View.VISIBLE

    }

    override fun onFailureLoanHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false

    }


    private fun loadMore()
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loanStatusModel  = ResLoanHistoryCurrent.LoanHistory(0,"",0.0,"",
                                    "","","",
                                    "","","","","",
                                    "","","","","","","","","","")

            listLoanHistoryCurrent!!.add(loanStatusModel)
            adapterLoanHistory.notifyItemInserted(listLoanHistoryCurrent!!.size-1)
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","current_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryPaginate(activity!!,jsonObject,after,this)

        }

   }

    override fun onClickList() {
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","current_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

}