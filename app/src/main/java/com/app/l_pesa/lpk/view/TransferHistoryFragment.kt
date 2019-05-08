package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterTransferHistory
import com.app.l_pesa.lpk.inter.ICallBackTransferHistory
import com.app.l_pesa.lpk.model.ResTransferHistory
import com.app.l_pesa.lpk.presenter.PresenterSavingsUnlock
import com.app.l_pesa.lpk.presenter.PresenterTransferHistory
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_transfer_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*


class TransferHistoryFragment : Fragment(), ICallBackTransferHistory {

    private lateinit var progressDialog                : KProgressHUD
    private lateinit var listTransferHistory           : ArrayList<ResTransferHistory.UserTransferHistory>
    private lateinit var adapterTransferHistory        : AdapterTransferHistory
    private lateinit var bottomSheetBehavior           : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""


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
        initLoader()
        swipeRefresh()

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

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
        adapterTransferHistory= AdapterTransferHistory(activity!!,listTransferHistory,this)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            loadTokenHistory("","")
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    private fun loadTokenHistory(from_date:String,to_date:String)
    {
        swipeRefreshLayout.isRefreshing = true
        val presenterTransferHistory = PresenterTransferHistory()
        presenterTransferHistory.getTokenHistory(activity!!,from_date,to_date,this)
    }

    fun doFilter()
    {

        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        {
            bottomSheetBehavior.state =(BottomSheetBehavior.STATE_HALF_EXPANDED)

        }
        else if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED)
        {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

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
                    //bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    val fromDate=CommonMethod.dateConvertYMD(etFromDate.text.toString())
                    val toDate  =CommonMethod.dateConvertYMD(etToDate.text.toString())

                    loadTokenHistory(fromDate!!,toDate!!)
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

    override fun onSuccessTransferHistory(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors?,from_date:String,to_date:String) {

        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.VISIBLE

        swipeRefreshLayout.isRefreshing = false
        activity!!.runOnUiThread {

            swipeRefreshLayout.isRefreshing = false

            listTransferHistory.clear()
            listTransferHistory.addAll(userTransferHistory)
            adapterTransferHistory      = AdapterTransferHistory(activity!!, listTransferHistory,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterTransferHistory

            hasNext =cursors!!.hasNext
            after   =cursors.after

            adapterTransferHistory.setLoadMoreListener(object : AdapterTransferHistory.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if(hasNext)
                        {
                            loadMore(from_date,to_date)
                        }

                    }

                }
            })


        }
    }

    private fun loadMore(from_date:String,to_date:String)
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val modelActionStatus= ResTransferHistory.ActionStatus(false, "")
            val loadModel  = ResTransferHistory.UserTransferHistory(0, 0, "","","","","",modelActionStatus)
            listTransferHistory.add(loadModel)
            adapterTransferHistory.notifyItemInserted(listTransferHistory.size-1)

            val presenterTransferHistory = PresenterTransferHistory()
            presenterTransferHistory.getTokenHistoryPaginate(activity!!,after,from_date,to_date,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessTransferHistoryPaginate(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, cursors: ResTransferHistory.Cursors,from_date:String,to_date:String) {

        swipeRefreshLayout.isRefreshing = false

        hasNext =cursors.hasNext
        after   =cursors.after
        if(listTransferHistory.size!=0)
        {
            try {

                listTransferHistory.removeAt(listTransferHistory.size - 1)
                adapterTransferHistory.notifyDataChanged()
                listTransferHistory.addAll(userTransferHistory)
                adapterTransferHistory.notifyItemRangeInserted(0, listTransferHistory.size)

            }
            catch (e:Exception)
            {}
        }
    }

    override fun onEmptyTransferHistory() {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorTransferHistory(message: String) {

        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSavingsUnlock(savings_id: String) {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            progressDialog.show()
            val jsonObject = JsonObject()
            jsonObject.addProperty("history_id",savings_id)
             val presenterSavingsUnlock= PresenterSavingsUnlock()
            presenterSavingsUnlock.doSavingsUnlock(activity!!,jsonObject,this)

        }
        else{

            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    private fun initLoader()
    {
        progressDialog= KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    override fun onSuccessSavingsUnlock() {

        dismiss()
        initData()

    }
    override fun onErrorSavingsUnlock(message: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}