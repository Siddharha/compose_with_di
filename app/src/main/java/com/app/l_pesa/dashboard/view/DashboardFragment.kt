package com.app.l_pesa.dashboard.view

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
import kotlinx.android.synthetic.main.dashboard_layout.*


class DashboardFragment: Fragment(), ICallBackDashboard {




    companion object {
        fun newInstance(): Fragment {
            return DashboardFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dashboard_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh()
        initData()
    }

    private fun swipeRefresh()
    {

        /*swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            initData()
        }*/
    }

    private fun initData()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
           // swipeRefreshLayout.isRefreshing = true
            val sharedPrefOBJ= SharedPref(activity!!)
            val presenterDashboard= PresenterDashboard()
            presenterDashboard.getDashboard(activity!!,sharedPrefOBJ.accessToken,this)
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }

    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {

       // swipeRefreshLayout.isRefreshing = false
        //left_header_txt.text=data.fixedDepositAmount
        //right_header_txt.text=data.savingsAmount
    }

    override fun onFailureDashboard(jsonMessage: String) {

       // swipeRefreshLayout.isRefreshing = false
    }
}

