package com.app.l_pesa.dashboard.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboard_layout.*
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.app.l_pesa.dashboard.adapter.LoanListAdapter
import com.app.l_pesa.dashboard.inter.ICallBackListOnClick
import com.app.l_pesa.loanHistory.view.LoanPaybackScheduledActivity


class DashboardFragment: Fragment(), ICallBackDashboard, ICallBackListOnClick{



    companion object {
        fun newInstance(): Fragment {
            return DashboardFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dashboard_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initUI()
        initData()
    }

    private fun initUI() {
        initSeekBar()
    }

    private fun initSeekBar() {
        seekBar.setOnTouchListener { _, _ -> true }

    }

    private fun swipeRefresh() {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            loadDashboard()
        }
    }

    private fun initData() {
        val sharedPrefOBJ = SharedPref(activity!!)
        val dashBoard = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)

        if (dashBoard != null) {
            setDashBoard(dashBoard)
        }


    }

    private fun loadDashboard() {
        if (CommonMethod.isNetworkAvailable(activity!!)) {
            swipeRefreshLayout.isRefreshing = true
            val sharedPrefOBJ = SharedPref(activity!!)
            val presenterDashboard = PresenterDashboard()
            presenterDashboard.getDashboard(activity!!, sharedPrefOBJ.accessToken, this)
        } else {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.no_internet))
        }
    }

    private fun setDashBoard(dashBoard: ResDashboard.Data) {

        setData(dashBoard)
    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {
        swipeRefreshLayout.isRefreshing = false
        setData(data)
    }

    override fun onFailureDashboard(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
    }

    private fun setData(dashBoard: ResDashboard.Data) {

        left_header_txt.text = dashBoard.fixedDepositAmount
        right_header_txt.text = dashBoard.savingsAmount

        seekBar.post{

            txt_start.text          = dashBoard.minCreditScore.toString()
            txt_max.text            = dashBoard.maxCreditScore.toString()

            seekBar.max = dashBoard.maxCreditScore.toFloat()
            seekBar.setProgress(dashBoard.creditScore.toFloat())

        }

            if (dashBoard.loans!!.size > 0) {
                loan_list.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                val adapterDashBoard = LoanListAdapter(dashBoard.loans!!, activity, rootLayout, this)
                loan_list.adapter = adapterDashBoard
                adapterDashBoard.notifyDataSetChanged()
            }

            val sharedPrefOBJ = SharedPref(activity!!)
            val gson = Gson()
            val dashBoardData = gson.toJson(dashBoard)
            sharedPrefOBJ.userDashBoard = dashBoardData

    }

    override fun onClickLoanList(type: String) {

        val sharedPref = SharedPref(activity!!)
        sharedPref.navigationTab = resources.getString(R.string.open_tab_loan)
        sharedPref.openTabLoan = type

        val intent = Intent(activity, DashboardActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onClickPay(type: String, loan_id: String) {

        val sharedPref = SharedPref(activity!!)
        sharedPref.payFullAmount = "A"
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE", type)
        bundle.putString("LOAN_ID", loan_id)
        val intent = Intent(activity, LoanPaybackScheduledActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent, bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }


}




