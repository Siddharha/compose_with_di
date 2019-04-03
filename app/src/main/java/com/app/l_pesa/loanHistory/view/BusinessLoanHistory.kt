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
import com.app.l_pesa.loanHistory.adapter.BusinessLoanHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackBusinessLoanHistory
import com.app.l_pesa.loanHistory.model.ResLoanHistoryBusiness
import com.app.l_pesa.loanHistory.presenter.PresenterLoanHistory
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_history_list.*
import java.util.ArrayList


class BusinessLoanHistory:Fragment(), ICallBackBusinessLoanHistory {


    private var listLoanHistoryBusiness         : ArrayList<ResLoanHistoryBusiness.LoanHistory>? = null
    private lateinit var adapterLoanHistory     : BusinessLoanHistoryAdapter

    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return BusinessLoanHistory()
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

            activity?.onBackPressed()
        }


    }

    private fun loadHistory()
    {
        listLoanHistoryBusiness      = ArrayList()
        adapterLoanHistory           = BusinessLoanHistoryAdapter(activity!!, listLoanHistoryBusiness!!,this)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","business_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryBusiness(activity!!,jsonObject,"",this)

        }


    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(com.app.l_pesa.R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            loadHistory()
        }
    }

    override fun onSuccessLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors, user_credit_score: Int) {


        cardView.visibility  = View.GONE
        rvLoan.visibility    = View.VISIBLE

        activity!!.runOnUiThread {
            hasNext =cursors.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            val shared=SharedPref(activity!!)
            shared.userCreditScore=user_credit_score.toString()
            listLoanHistoryBusiness!!.clear()
            listLoanHistoryBusiness!!.addAll(loan_historyBusiness)
            adapterLoanHistory          = BusinessLoanHistoryAdapter(activity!!, listLoanHistoryBusiness!!,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rvLoan.layoutManager        = llmOBJ
            rvLoan.adapter              = adapterLoanHistory

            adapterLoanHistory.setLoadMoreListener(object : BusinessLoanHistoryAdapter.OnLoadMoreListener {
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

    override fun onSuccessPaginateLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors) {

        hasNext =cursors.hasNext
        after   =cursors.after
        if(listLoanHistoryBusiness!!.size!=0)
        {
            try {

                listLoanHistoryBusiness!!.removeAt(listLoanHistoryBusiness!!.size - 1)
                adapterLoanHistory.notifyDataChanged()
                listLoanHistoryBusiness!!.addAll(loan_historyBusiness)
                adapterLoanHistory.notifyItemRangeInserted(0, listLoanHistoryBusiness!!.size)

            }
            catch (e:Exception)
            {}
        }


    }

    override fun onEmptyPaginateLoanHistory() {


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
            val loanStatusModel  = ResLoanHistoryBusiness.LoanHistory(0,"",0.0,"",
                    "","","",
                    "","","","","",
                    "","","","","","","","","","")

            listLoanHistoryBusiness!!.add(loanStatusModel)
            adapterLoanHistory.notifyItemInserted(listLoanHistoryBusiness!!.size-1)
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","business_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryPaginateBusiness(activity!!,jsonObject,after,this)

        }

    }

    override fun onClickList() {
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","business_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

}