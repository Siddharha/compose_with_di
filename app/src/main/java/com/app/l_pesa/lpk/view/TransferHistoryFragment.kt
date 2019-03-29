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
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterTransferHistory
import com.app.l_pesa.lpk.inter.ICallBackTransferHistory
import com.app.l_pesa.lpk.model.ResTransferHistory
import com.app.l_pesa.lpk.presenter.PresenterTransferHistory
import kotlinx.android.synthetic.main.fragment_transfer_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*


class TransferHistoryFragment : Fragment(), ICallBackTransferHistory {

    private var listTransferHistory           : ArrayList<ResTransferHistory.UserTransferHistory>? = null
    private var adapterTransferHistory        : AdapterTransferHistory?                            = null

    private var hasNext=false
    private var after=""

    private var mBottomSheetBehavior1: BottomSheetBehavior<*>? = null
    companion object {
        fun newInstance(): Fragment {
            return TransferHistoryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_transfer_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        swipeRefresh()

        mBottomSheetBehavior1 = BottomSheetBehavior.from<View>(bottom_sheet)
        mBottomSheetBehavior1!!.isHideable=true
        mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_HIDDEN

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
        listTransferHistory= ArrayList()
        adapterTransferHistory= AdapterTransferHistory(activity!!,listTransferHistory!!)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterTransferHistory = PresenterTransferHistory()
            presenterTransferHistory.getTokenHistory(activity!!,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    fun doFilter()
    {

        if(mBottomSheetBehavior1!!.state == BottomSheetBehavior.STATE_HIDDEN)
        {
            mBottomSheetBehavior1!!.setState(BottomSheetBehavior.STATE_EXPANDED)

        }
        else
        {
            mBottomSheetBehavior1!!.setState(BottomSheetBehavior.STATE_HIDDEN)

        }

        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }
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

    override fun onSuccessTransferHistory(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors?) {

        swipeRefreshLayout.isRefreshing = false
        activity!!.runOnUiThread {

            swipeRefreshLayout.isRefreshing = false

            listTransferHistory!!.clear()
            listTransferHistory!!.addAll(userTransferHistory)
            adapterTransferHistory      = AdapterTransferHistory(activity!!, listTransferHistory!!)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterTransferHistory

            hasNext =cursors!!.hasNext
            after   =cursors.after

            adapterTransferHistory!!.setLoadMoreListener(object : AdapterTransferHistory.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if(hasNext)
                        {
                            loadMore()
                        }

                    }

                }
            })


        }
    }

    private fun loadMore()
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loadModel  = ResTransferHistory.UserTransferHistory(0, 0, "","","","","")
            listTransferHistory!!.add(loadModel)
            adapterTransferHistory!!.notifyItemInserted(listTransferHistory!!.size-1)

            val presenterTransferHistory = PresenterTransferHistory()
            presenterTransferHistory.getTokenHistoryPaginate(activity!!,after,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessTransferHistoryPaginate(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors) {

        swipeRefreshLayout.isRefreshing = false

        hasNext =cursors.hasNext
        after   =cursors.after
        if(listTransferHistory!!.size!=0)
        {
            try {

                listTransferHistory!!.removeAt(listTransferHistory!!.size - 1)
                adapterTransferHistory!!.notifyDataChanged()
                listTransferHistory!!.addAll(userTransferHistory)
                adapterTransferHistory!!.notifyItemRangeInserted(0, listTransferHistory!!.size)

            }
            catch (e:Exception)
            {}
        }
    }

    override fun onEmptyTransferHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorTransferHistory(message: String) {

        swipeRefreshLayout.isRefreshing = false
    }
}