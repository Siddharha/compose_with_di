package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonClass
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.lpk.adapter.AdapterTransferHistory
import com.app.l_pesa.lpk.inter.ICallBackTransferHistory
import com.app.l_pesa.lpk.model.ResTransferHistory
import com.app.l_pesa.lpk.presenter.PresenterSavingsUnlock
import com.app.l_pesa.lpk.presenter.PresenterTransferHistory
import com.app.l_pesa.main.MainActivity
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
           etFromDate.text!!.clear()
           etToDate.text!!.clear()
           initData()
        }
    }

    private fun initData()
    {
        listTransferHistory= ArrayList()
        adapterTransferHistory= AdapterTransferHistory(activity!!,listTransferHistory,this)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            loadTokenHistory("","","DEFAULT")
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }


    }

    private fun loadTokenHistory(from_date:String,to_date:String, type:String)
    {
        swipeRefreshLayout.isRefreshing = true
        val presenterTransferHistory = PresenterTransferHistory()
        presenterTransferHistory.getTokenHistory(activity!!,from_date,to_date,type,this)
    }

    fun doFilter()
    {

        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.state =(BottomSheetBehavior.STATE_EXPANDED)

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
                    loadTokenHistory(fromDate!!,toDate!!,"FILTER")
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

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(activity,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(activity,etToDate)
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

    override fun onSessionTimeOut(message: String) {
        dismiss()
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(activity!!)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity!!, MainActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    activity!!.finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }

    override fun onEmptyTransferHistory(type:String) {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        if(type=="FILTER")
        {
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        else
        {
            txt_message.text = resources.getString(R.string.empty_transfer_history_message)
        }
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