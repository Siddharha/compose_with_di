package com.app.l_pesa.lpk.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterInterestHistory
import com.app.l_pesa.lpk.inter.ICallBackInterestHistory
import com.app.l_pesa.lpk.model.ResInterestHistory
import com.app.l_pesa.lpk.presenter.PresenterInterestHistory
import kotlinx.android.synthetic.main.layout_recycler.*
import java.util.ArrayList

class InterestHistoryFragment : Fragment(), ICallBackInterestHistory {


    companion object {
        fun newInstance(): Fragment {
            return InterestHistoryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        swipeRefresh()

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            initData()
        }
    }

    private fun initData()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterInterestHistory = PresenterInterestHistory()
            presenterInterestHistory.getInterestHistory(activity!!,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    fun doFilter()
    {
        Toast.makeText(activity,"TWO",Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessInterestHistory(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?) {

        swipeRefreshLayout.isRefreshing = false
        val adapterInterestHistory = AdapterInterestHistory(activity!!, userInterestHistory!!)
        rlList.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rlList.adapter = adapterInterestHistory
    }

    override fun onEmptyInterestHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorInterestHistory(message: String) {

        swipeRefreshLayout.isRefreshing = false
    }
}