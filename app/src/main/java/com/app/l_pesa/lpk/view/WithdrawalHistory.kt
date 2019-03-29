package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterWithdrawalHistory
import com.app.l_pesa.lpk.inter.ICallBackWithdrawalHistory
import com.app.l_pesa.lpk.model.ResWithdrawalHistory
import com.app.l_pesa.lpk.presenter.PresenterWithdrawalHistory
import kotlinx.android.synthetic.main.fragment_withdrawal_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*
import kotlin.collections.ArrayList


class WithdrawalHistory:Fragment() , ICallBackWithdrawalHistory {

    private var listWithdrawalHistory           : ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>? = null
    private var adapterWithdrawalHistory        : AdapterWithdrawalHistory?                   = null
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    companion object {
        fun newInstance(): Fragment {
            return WithdrawalHistory()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_withdrawal_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initData()

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior!!.isHideable=true
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN

    }

    private fun initData()
    {
        listWithdrawalHistory       = ArrayList()
        adapterWithdrawalHistory    = AdapterWithdrawalHistory(activity!!, listWithdrawalHistory)

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing=true
            val presenterWithdrawalHistory= PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getInterestHistory(activity!!,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing=false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            initData()

        }
    }

    fun doFilter()
    {
        if(bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_HIDDEN)
        {
            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)

        }
        else
        {
            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_HIDDEN)

        }

        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_HIDDEN)

        }
    }

    override fun onSuccessWithdrawalHistory(userWithdrawalHistory: ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>) {

        swipeRefreshLayout.isRefreshing=false
        listWithdrawalHistory!!.clear()
        listWithdrawalHistory!!.addAll(userWithdrawalHistory)
        adapterWithdrawalHistory    = AdapterWithdrawalHistory(activity!!, listWithdrawalHistory)
        val llmOBJ                  = LinearLayoutManager(activity)
        llmOBJ.orientation          = LinearLayoutManager.VERTICAL
        rlList.layoutManager        = llmOBJ
        rlList.adapter              = adapterWithdrawalHistory


        adapterWithdrawalHistory!!.setLoadMoreListener(object : AdapterWithdrawalHistory.OnLoadMoreListener {
            override fun onLoadMore() {

                rlList.post {

                    /*if(hasNext)
                    {*/
                       // loadMore()
                   // }

                }

            }
        })
    }

    private fun loadMore()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loadModel  = ResWithdrawalHistory.UserWithdrawalHistory(0, 0,  "",
                    "", "", "",
                    "", "", "", "", "","","")

            listWithdrawalHistory!!.add(loadModel)
            adapterWithdrawalHistory!!.notifyItemInserted(listWithdrawalHistory!!.size-1)



        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onEmptyWithdrawalHistory() {

        swipeRefreshLayout.isRefreshing=false
    }

    override fun onErrorWithdrawalHistory(message: String) {

        swipeRefreshLayout.isRefreshing=false
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)+1
        val day     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            if(dayOfMonth.toString().length==1)
            {
                if(monthOfYear.toString().length==1)
                {
                    etFromDate.setText("0$dayOfMonth-0$month-$year")
                }
                else
                {
                    etFromDate.setText("0$dayOfMonth-$month-$year")
                }

            }
            else
            {
                if(monthOfYear.toString().length==1)
                {
                    etFromDate.setText("$dayOfMonth-0$month-$year")
                }
                else
                {
                    etFromDate.setText("$dayOfMonth-$monthOfYear-$year")
                }

            }

        }, year, month, day)

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)+1
        val day     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            if(dayOfMonth.toString().length==1)
            {
                if(monthOfYear.toString().length==1)
                {
                    etToDate.setText("0$dayOfMonth-0$month-$year")
                }
                else
                {
                    etToDate.setText("0$dayOfMonth-$month-$year")
                }

            }
            else
            {
                if(monthOfYear.toString().length==1)
                {
                    etToDate.setText("$dayOfMonth-0$month-$year")
                }
                else
                {
                    etToDate.setText("$dayOfMonth-$monthOfYear-$year")
                }

            }

        }, year, month, day)

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }
}