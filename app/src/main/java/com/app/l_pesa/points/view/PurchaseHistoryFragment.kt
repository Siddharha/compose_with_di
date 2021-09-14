package com.app.l_pesa.points.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.points.adapters.PurchaseCreditHistoryListAdapter
import com.app.l_pesa.points.models.CreditPlanHistoryResponse
import com.app.l_pesa.points.inter.ICallBackCreditPlanHistory
import com.app.l_pesa.points.presenter.PresenterCreditHistory
import kotlinx.android.synthetic.main.fragment_purchase_history.view.*


class PurchaseHistoryFragment : Fragment(), ICallBackCreditPlanHistory {
    lateinit var rootView:View
    private var hasNext=false
    private var after=""
    private val presenterCreditHistory: PresenterCreditHistory by lazy { PresenterCreditHistory() }
    private val pref:SharedPref by lazy { SharedPref(requireContext()) }
    private val mLayoutManager:LinearLayoutManager by lazy { LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false) }
    private val creditLoanHistory:ArrayList<CreditPlanHistoryResponse.Data.UserBuyPoints> by lazy { ArrayList() }
    private val purchaseCreditHistoryListAdapter:PurchaseCreditHistoryListAdapter by lazy {
        PurchaseCreditHistoryListAdapter(requireContext(),creditLoanHistory,this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_purchase_history, container, false)
        initialize()
        onActionPerform()
        getAllPlanHistory()
        return rootView
    }

    private fun onActionPerform() {
        rootView.srHistory.setOnRefreshListener {
                getAllPlanHistory()
            }
//        rootView.apply {
//            srHistory.setOnRefreshListener {
//                getAllPlanHistory()
//            }
//        }
    }

    private fun initialize(){

    }

    @SuppressLint("CheckResult")
    private fun getAllPlanHistory() {
        presenterCreditHistory.getCreditHistory(requireContext(),"","","","",this)
    }
    override fun onSuccessCreditHistory(
        loan_historyCredit: List<CreditPlanHistoryResponse.Data.UserBuyPoints>,
        cursors: CreditPlanHistoryResponse.Data.Cursors,
        from_date: String,
        to_date: String
    ) {
        hasNext =cursors.hasNext
        after   =cursors.after




        requireActivity().runOnUiThread {

            rootView.apply {
                rvPurchaseHistory.apply {
                    layoutManager = mLayoutManager
                    adapter = purchaseCreditHistoryListAdapter
                }
                    if (srHistory.isRefreshing){
                        srHistory.isRefreshing = false
                    }


                creditLoanHistory.clear()
                creditLoanHistory.addAll(loan_historyCredit)

                purchaseCreditHistoryListAdapter.setLoadMoreListener(object :
                    PurchaseCreditHistoryListAdapter.OnLoadMoreListener {
                    override fun onLoadMore() {

                        rvPurchaseHistory.post {

                            if (hasNext) {
                                loadMore(from_date, to_date)
                            }

                        }

                    }
                })
            }
        }

    }

    private fun loadMore(fromDate: String, toDate: String) {
        presenterCreditHistory.getCreditHistoryPaginate(requireContext(),fromDate,toDate,after,this)
    }

    override fun onSuccessPaginateCreditHistory(
        loan_historyCredit: List<CreditPlanHistoryResponse.Data.UserBuyPoints>,
        cursors: CreditPlanHistoryResponse.Data.Cursors,
        from_date: String,
        to_date: String
    ) {
        rootView.apply {
            if (srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }
        }
        rootView.tvNoHistory.visibility = View.GONE
        hasNext =cursors.hasNext
        after   =cursors.after
        if(creditLoanHistory.size!=0)
        {
            try {

                creditLoanHistory.removeAt(creditLoanHistory.size - 1)
                purchaseCreditHistoryListAdapter.notifyDataChanged()
                creditLoanHistory.addAll(loan_historyCredit)
                purchaseCreditHistoryListAdapter.notifyItemRangeInserted(0, creditLoanHistory.size)

            }
            catch (e:Exception)
            {}
        }

        if(creditLoanHistory.isEmpty()) {
            rootView.tvNoHistory.visibility = View.VISIBLE
            rootView.tvNoHistory.text = "No History found!"
        }else{
            rootView.tvNoHistory.visibility = View.GONE
        }
    }

    override fun onEmptyCreditHistory(type: String) {
        rootView.apply {
            if (srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }

            if(creditLoanHistory.isEmpty()) {
                rootView.tvNoHistory.visibility = View.VISIBLE
                rootView.tvNoHistory.text = "No History found!"
            }else{
                rootView.tvNoHistory.visibility = View.GONE
            }
        }
    }

    override fun onFailureLoanHistory(jsonMessage: String) {
        rootView.apply {
            if (srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }
            tvNoHistory.visibility = View.VISIBLE
            tvNoHistory.text = jsonMessage
        }


    }

    override fun onClickList() {

    }

    override fun onRemoveCredit(
        position: Int,
        loanHistoryCredit: CreditPlanHistoryResponse.Data.UserBuyPoints
    ) {
        rootView.apply {
            if (srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }
        }
    }

    override fun onSuccessRemoveCredit(position: Int) {
        rootView.apply {
            if (srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }
        }
    }

    override fun onFailureRemoveCredit(message: String) {
        rootView.apply {
            if (srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }
        }
    }

    override fun onSessionTimeOut(message: String) {
        rootView.apply {
            if (!srHistory.isRefreshing){
                srHistory.isRefreshing = false
            }
        }
    }


}