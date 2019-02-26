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

    private var listLoanHistory                 : ArrayList<ResLoanHistory.LoanHistory>? = null

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
        listLoanHistory= ArrayList()

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
        listLoanHistory!!.clear()
        listLoanHistory!!.addAll(loanHistory)
        val currentLoanAdapter          = CurrentLoanHistoryAdapter(activity!!, listLoanHistory!!)
        rvLoan.layoutManager            = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter                  = currentLoanAdapter
    }

    override fun onEmptyLoanHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onFailureLoanHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
    }


    private fun loadMore()
    {

        val loanStatus         = ResLoanHistory.LoanStatus(false)
        val listLoanStatus     = ArrayList<ResLoanHistory.LoanStatus>()
        listLoanStatus.add(loanStatus)

        val loanStatusModel    = ResLoanHistory.LoanHistory(0,"",0,"",
                                                            "","","",
                                                            "","","","","",
                                                            "","" +
                                                            "",listLoanStatus)

        listLoanHistory!!.add(loanStatusModel)
        /*val blockModel              = VisitorInsideBlock("")
        val unitModel               = VisitorUnit("","","",0,"",0,blockModel)
        val visitorGatePassUnit     = VisitorGatePassUnit("")
        val visitorGatePass         = VisitorGatePass(0,"","","",visitorGatePassUnit)
        val unitAssociationModel    = VisitorUnitAssociations("",unitModel)
        val unitAssociationList     = ArrayList<VisitorUnitAssociations>()
        val visitorGatePassList     = ArrayList<VisitorGatePass>()
        visitorGatePassList.add(visitorGatePass)
        unitAssociationList.add(unitAssociationModel)
        val visitorDataModel        = VisitorInsideLogs(0,"","","","","",0,"","",
                visitorModel,unitAssociationList,visitorGatePassList)

        val jsonObject = JSONObject()
        jsonObject.accumulate("date",date)

        val jsonParser              =  JsonParser()
        val jsonRequest  =  jsonParser.parse(jsonObject.toString())

        visitorApprovedList!!.add(visitorDataModel)
        visitorLogAdapterOBJ!!.notifyItemInserted(visitorApprovedList!!.size-1)
        val visitorLogPresenterOBJ  =  VisitorLogPresenter()
        visitorLogPresenterOBJ.visitorLogPagination(activity,accessToken,unitId,"REGULAR",cursorAFTER,jsonRequest.asJsonObject,this)*/


    }
}