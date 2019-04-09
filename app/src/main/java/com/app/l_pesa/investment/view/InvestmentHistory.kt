package com.app.l_pesa.investment.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.investment.adapter.AdapterWindowInvestmentHistory
import com.app.l_pesa.investment.adapter.InvestmentHistoryAdapter
import com.app.l_pesa.investment.inter.ICallBackEditHistory
import com.app.l_pesa.investment.inter.ICallBackInvestmentHistory
import com.app.l_pesa.investment.inter.ICallBackPopUpWindow
import com.app.l_pesa.investment.model.ModelWindowHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import com.app.l_pesa.investment.presenter.PresenterInvestmentHistory
import com.app.l_pesa.investment.presenter.PresenterInvestmentReinvestment
import com.app.l_pesa.investment.presenter.PresenterInvestmentWithdrawal
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.ArrayList

class InvestmentHistory:Fragment(),ICallBackInvestmentHistory, ICallBackEditHistory {



    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1

    private var listInvestment                        : ArrayList<ResInvestmentHistory.UserInvestment>? = null
    private lateinit var adapterInvestmentHistory     : InvestmentHistoryAdapter

    private var hasNext=false
    private var after=""

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
        listInvestment      = ArrayList()
        adapterInvestmentHistory   = InvestmentHistoryAdapter(activity!!, listInvestment!!,this)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistory(activity!!,this)
        }
    }

    private fun dismissPopup() {
        filterPopup?.let {
            if(it.isShowing){
                it.dismiss()
            }
            filterPopup = null
        }

    }

    override fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?) {

        activity!!.runOnUiThread {
            hasNext =cursors!!.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            listInvestment!!.clear()
            listInvestment!!.addAll(userInvestment)
            adapterInvestmentHistory    = InvestmentHistoryAdapter(activity!!, listInvestment!!,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rvLoan.layoutManager        = llmOBJ
            rvLoan.adapter              = adapterInvestmentHistory

            adapterInvestmentHistory.setLoadMoreListener(object : InvestmentHistoryAdapter.OnLoadMoreListener {
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

    override fun onSuccessInvestmentHistoryPaginate(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?) {

        swipeRefreshLayout.isRefreshing    = false
        hasNext =cursors!!.hasNext
        after   =cursors.after
        if(listInvestment!!.size!=0)
        {
            try {

                listInvestment!!.removeAt(listInvestment!!.size - 1)
                adapterInvestmentHistory.notifyDataChanged()
                listInvestment!!.addAll(userInvestment)
                adapterInvestmentHistory.notifyItemRangeInserted(0, listInvestment!!.size)

            }
            catch (e:Exception)
            {}
        }
    }

    private fun loadMore()
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val actionStatusModel= ResInvestmentHistory.ActionState(false,false,false,"","")
            val loanStatusModel  = ResInvestmentHistory.UserInvestment(0, 0,0,0,"","","","",
                                   "","","","",0.0,0.0,0.0,0.0,actionStatusModel)

            listInvestment!!.add(loanStatusModel)
            adapterInvestmentHistory.notifyItemInserted(listInvestment!!.size-1)

            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistoryPaginate(activity!!,after,this)

        }

    }


    override fun onEmptyInvestmentHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorInvestmentHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity,jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onEditWindow(imgEdit: ImageButton, investmentList: ResInvestmentHistory.UserInvestment) {

        dismissPopup()
        filterPopup = showAlertFilter(investmentList)
        filterPopup?.isOutsideTouchable = true
        filterPopup?.isFocusable = true
        filterPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       // filterPopup?.showAsDropDown(rootLayout,150,-420)
        filterPopup?.showAsDropDown(imgEdit)
    }


    @SuppressLint("InflateParams")
    private fun showAlertFilter(investmentList: ResInvestmentHistory.UserInvestment): PopupWindow {

        val filterItemList = mutableListOf<ModelWindowHistory>()

        if(investmentList.actionState.btnWithdrawalShow)
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.withdrawal),investmentList.actionState.btnWithdrawalShow))
        }
        if(investmentList.actionState.btnReinvestShow)
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.reinvestment),investmentList.actionState.btnReinvestShow))
        }
        if(investmentList.actionState.btnExitPointShow)
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.exit_point),investmentList.actionState.btnExitPointShow))
        }


        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_only_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val adapter = AdapterWindowInvestmentHistory(activity!!)
        adapter.addAlertFilter(filterItemList)
        recyclerView.adapter = adapter
        adapter.selectedItem(selectedItem)

        adapter.setOnClick(object : ICallBackPopUpWindow {
            override fun onItemClick(view: View, position: Int, modelWindowHistory: String) {
                selectedItem = position

                if(investmentList.actionState.btnExitPointStatus=="disable" && modelWindowHistory==resources.getString(R.string.exit_point))
                {
                  Toast.makeText(activity,investmentList.actionState.btnExitPointStatusMessage,Toast.LENGTH_SHORT).show()
                }
                else if(modelWindowHistory==resources.getString(R.string.withdrawal))
                {
                  if(CommonMethod.isNetworkAvailable(activity!!))
                  {

                     investmentWithdrawal(investmentList.investment_id.toString())
                  }
                  else
                  {
                     CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                  }
                }
                else if(modelWindowHistory==resources.getString(R.string.reinvestment))
                {
                    if(CommonMethod.isNetworkAvailable(activity!!))
                    {

                        investmentReinvest(investmentList.investment_id.toString())
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                    }
                }
                dismissPopup()
            }
        })

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun investmentWithdrawal(investment_id: String)
    {
        swipeRefreshLayout.isRefreshing = true
        val jsonObject = JsonObject()
        jsonObject.addProperty("deposit_id",investment_id)
        val presenterInvestmentWithdrawal= PresenterInvestmentWithdrawal()
        presenterInvestmentWithdrawal.doInvestmentWithdrawal(activity!!,this,jsonObject)
    }

    private fun investmentReinvest(investment_id: String)
    {
        swipeRefreshLayout.isRefreshing = true
        val jsonObject = JsonObject()
        jsonObject.addProperty("deposit_id",investment_id)
        val presenterInvestmentReinvestment= PresenterInvestmentReinvestment()
        presenterInvestmentReinvestment.doReinvestment(activity!!,this,jsonObject)
    }

    override fun onSuccessInvestmentWithdrawal() {

        swipeRefresh()
    }

    override fun onErrorInvestmentWithdrawal(message: String) {

        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSuccessReinvestment() {
        swipeRefresh()
    }

    override fun onErrorReinvestment(message: String) {
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}