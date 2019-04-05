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
import com.app.l_pesa.investment.model.ModelWindowHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import com.app.l_pesa.investment.presenter.PresenterInvestmentHistory
import com.app.l_pesa.profile.inter.ICallBackRecyclerCallbacks
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.ArrayList

class InvestmentHistory:Fragment(),ICallBackInvestmentHistory, ICallBackEditHistory {

    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1

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

    override fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>) {

        swipeRefreshLayout.isRefreshing    = false
        val investmentHistoryAdapter       = InvestmentHistoryAdapter(activity!!, userInvestment,this)
        rvLoan.layoutManager               = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvLoan.adapter                     = investmentHistoryAdapter
    }

    override fun onEmptyInvestmentHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorInvestmentHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity,jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onEditWindow(imgEdit: ImageButton) {

        dismissPopup()
        filterPopup = showAlertFilter()
        filterPopup?.isOutsideTouchable = true
        filterPopup?.isFocusable = true
        filterPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       // filterPopup?.showAsDropDown(rootLayout,150,-420)
        filterPopup?.showAsDropDown(imgEdit)
    }


    @SuppressLint("InflateParams")
    private fun showAlertFilter(): PopupWindow {

        val filterItemList = mutableListOf<ModelWindowHistory>()

        filterItemList.add(ModelWindowHistory(resources.getString(R.string.withdrawal)))
        filterItemList.add(ModelWindowHistory(resources.getString(R.string.reinvestment)))
        filterItemList.add(ModelWindowHistory(resources.getString(R.string.exit_point)))

        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_only_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val adapter = AdapterWindowInvestmentHistory(activity!!)
        adapter.addAlertFilter(filterItemList)
        recyclerView.adapter = adapter
        adapter.selectedItem(selectedItem)

        adapter.setOnClick(object : ICallBackRecyclerCallbacks<ModelWindowHistory> {
            override fun onItemClick(view: View, position: Int, item:ModelWindowHistory) {
                selectedItem = position

                dismissPopup()
            }
        })

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}