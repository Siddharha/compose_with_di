package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterWithdrawalHistory
import com.app.l_pesa.lpk.inter.ICallBackWithdrawalHistory
import com.app.l_pesa.lpk.model.ResWithdrawalHistory
import com.app.l_pesa.lpk.presenter.PresenterWithdrawalHistory
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_withdrawal_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*


class WithdrawalHistoryFragment: Fragment() , ICallBackWithdrawalHistory {


    private lateinit var listWithdrawalHistory                      : ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>
    private lateinit var adapterWithdrawalHistory                   : AdapterWithdrawalHistory
    private lateinit var bottomSheetBehavior                        : BottomSheetBehavior<*>
    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return WithdrawalHistoryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_withdrawal_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initData("","","")

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    }

    private fun initData(from_date:String,to_date:String,type:String)
    {
        listWithdrawalHistory       = ArrayList()
        adapterWithdrawalHistory    = AdapterWithdrawalHistory(activity!!, listWithdrawalHistory)

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val logger = AppEventsLogger.newLogger(activity)
            val params =  Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Withdrawal History")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

            swipeRefreshLayout.isRefreshing=true
            val presenterWithdrawalHistory= PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getWithdrawalHistory(activity!!,from_date,to_date,type,this)
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
            etFromDate.text!!.clear()
            etToDate.text!!.clear()
            initData("","","DEFAULT")

        }
    }

    fun doFilter()
    {
        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.state =(BottomSheetBehavior.STATE_EXPANDED)
                resetFilter()

            }
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }


        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }

        buttonFilterSubmit.setOnClickListener {

            if(TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.you_have_select_from_date_to_date))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    val fromDate=CommonMethod.dateConvertYMD(etFromDate.text.toString())
                    val toDate  =CommonMethod.dateConvertYMD(etToDate.text.toString())
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    initData(fromDate!!,toDate!!,"FILTER")
                }
                else
                {
                    swipeRefreshLayout.isRefreshing = false
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }

            }


        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }
        imgCleanFrom_date.setOnClickListener {

            etFromDate.text!!.clear()

        }

        imgCleanTo_date.setOnClickListener {

            etToDate.text!!.clear()

        }
    }

    private fun resetFilter()
    {
        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }
    }


    override fun onSuccessWithdrawalHistory(userWithdrawalHistory: java.util.ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>, cursors: ResWithdrawalHistory.Cursors?, from_date: String, to_date: String) {

        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.VISIBLE

        activity!!.runOnUiThread {

            hasNext = cursors!!.hasNext
            after = cursors.after
            swipeRefreshLayout.isRefreshing = false
            listWithdrawalHistory.clear()
            listWithdrawalHistory.addAll(userWithdrawalHistory)
            adapterWithdrawalHistory = AdapterWithdrawalHistory(activity!!, listWithdrawalHistory)
            val llmOBJ = LinearLayoutManager(activity)
            rlList.layoutManager = llmOBJ
            rlList.adapter = adapterWithdrawalHistory


            adapterWithdrawalHistory.setLoadMoreListener(object : AdapterWithdrawalHistory.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if (hasNext)
                        {
                            loadMore(from_date,to_date)
                        }

                    }

                }
            })
        }
    }

    override fun onSuccessWithdrawalHistoryPaginate(userWithdrawalHistory: java.util.ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>, cursors: ResWithdrawalHistory.Cursors?,from_date:String,to_date:String) {

        hasNext =cursors!!.hasNext
        after   =cursors.after
        if(listWithdrawalHistory.size!=0)
        {
            try {

                listWithdrawalHistory.removeAt(listWithdrawalHistory.size - 1)
                adapterWithdrawalHistory.notifyDataChanged()
                listWithdrawalHistory.addAll(userWithdrawalHistory)
                adapterWithdrawalHistory.notifyItemRangeInserted(0, listWithdrawalHistory.size)

            }
            catch (e:Exception)
            {}
        }
    }

    private fun loadMore(from_date:String,to_date:String)
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loadModel  = ResWithdrawalHistory.UserWithdrawalHistory(0, 0,  "",
                    "", "", "",
                    "", "", "", "", "","","")

            listWithdrawalHistory.add(loadModel)
            adapterWithdrawalHistory.notifyItemInserted(listWithdrawalHistory.size-1)

            val presenterWithdrawalHistory= PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getWithdrawalHistoryPaginate(activity!!,after,from_date,to_date,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onEmptyWithdrawalHistory(type: String) {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        if(type=="FILTER")
        {
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        else
        {
            txt_message.text = resources.getString(R.string.empty_loan_history_message)
        }
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorWithdrawalHistory(message: String) {

        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        CommonMethod.datePicker(activity!!,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        CommonMethod.datePicker(activity!!,etToDate)
    }
}