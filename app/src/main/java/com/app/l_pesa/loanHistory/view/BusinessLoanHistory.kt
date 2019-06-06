package com.app.l_pesa.loanHistory.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonClass
import com.app.l_pesa.common.CommonEditTextRegular
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.adapter.BusinessLoanHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackBusinessLoanHistory
import com.app.l_pesa.loanHistory.model.ResLoanHistoryBusiness
import com.app.l_pesa.loanHistory.presenter.PresenterLoanHistory
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_loan_history_list.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*


class BusinessLoanHistory:Fragment(), ICallBackBusinessLoanHistory {

    private lateinit  var progressDialog: KProgressHUD
    private var listLoanHistoryBusiness         : ArrayList<ResLoanHistoryBusiness.LoanHistory>? = null
    private lateinit var adapterLoanHistory     : BusinessLoanHistoryAdapter
    private lateinit var bottomSheetBehavior    : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return BusinessLoanHistory()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_history_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoader()
        loadHistory("","","DEFAULT")
        swipeRefresh()
        buttonApplyLoan.setOnClickListener {

            val sharedPref = SharedPref(activity!!)
            sharedPref.navigationTab = resources.getString(R.string.open_tab_loan)
            sharedPref.openTabLoan = "BUSINESS"
            val intent = Intent(activity, DashboardActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.left_in, R.anim.right_out)
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

    private fun dismissDialog() {

        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }

    }

    private fun loadHistory(from_date: String, to_date: String,type:String)
    {
        listLoanHistoryBusiness      = ArrayList()
        adapterLoanHistory           = BusinessLoanHistoryAdapter(activity!!, listLoanHistoryBusiness!!,this)
        bottomSheetBehavior          = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","business_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryBusiness(activity!!,jsonObject,from_date,to_date,type,"",this)

        }

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            etFromDate.text!!.clear()
            etToDate.text!!.clear()
            loadHistory("","","DEFAULT")
        }
    }

    override fun onSuccessLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors, user_credit_score: Int, from_date: String, to_date: String) {


        cardView.visibility  = View.GONE
        rvLoan.visibility    = View.VISIBLE

        activity!!.runOnUiThread {
            hasNext =cursors.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            val shared=SharedPref(activity!!)
            shared.userCreditScore=user_credit_score.toString()
            listLoanHistoryBusiness!!.clear()
            listLoanHistoryBusiness!!.addAll(loan_historyBusiness)
            adapterLoanHistory          = BusinessLoanHistoryAdapter(activity!!, listLoanHistoryBusiness!!,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rvLoan.layoutManager        = llmOBJ
            rvLoan.adapter              = adapterLoanHistory

            adapterLoanHistory.setLoadMoreListener(object : BusinessLoanHistoryAdapter.OnLoadMoreListener {
                override fun onLoadMore() {

                    rvLoan.post {

                        if(hasNext)
                        {
                            loadMore(from_date,to_date)
                        }

                    }

                }
            })

        }

    }

    override fun onSuccessPaginateLoanHistory(loan_historyBusiness: ArrayList<ResLoanHistoryBusiness.LoanHistory>, cursors: ResLoanHistoryBusiness.Cursors, from_date: String, to_date: String) {

        hasNext =cursors.hasNext
        after   =cursors.after
        if(listLoanHistoryBusiness!!.size!=0)
        {
            try {

                listLoanHistoryBusiness!!.removeAt(listLoanHistoryBusiness!!.size - 1)
                adapterLoanHistory.notifyDataChanged()
                listLoanHistoryBusiness!!.addAll(loan_historyBusiness)
                adapterLoanHistory.notifyItemRangeInserted(0, listLoanHistoryBusiness!!.size)

            }
            catch (e:Exception)
            {}
        }


    }


    override fun onEmptyLoanHistory(type: String) {

        swipeRefreshLayout.isRefreshing = false
        rvLoan.visibility  =View.GONE
        if(type=="FILTER")
        {
            buttonApplyLoan.visibility=View.GONE
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        cardView.visibility=View.VISIBLE

    }

    override fun onFailureLoanHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false

    }

    private fun loadMore(from_date: String, to_date: String)
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loanStatusModel  = ResLoanHistoryBusiness.LoanHistory(0,"",0.0,"",
                    "","","",
                    "","","","","",
                    "","","","","","","","","","")

            listLoanHistoryBusiness!!.add(loanStatusModel)
            adapterLoanHistory.notifyItemInserted(listLoanHistoryBusiness!!.size-1)
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","business_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryPaginateBusiness(activity!!,jsonObject,from_date,to_date,after,this)

        }

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

            if (TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString())) {
                CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.you_have_select_from_date_to_date))
            } else {

                val fromDate = CommonMethod.dateConvertYMD(etFromDate.text.toString())
                val toDate = CommonMethod.dateConvertYMD(etToDate.text.toString())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                loadHistory(fromDate!!,toDate!!,"FILTER")

            }
        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

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
        commonClass.datePicker(activity!!,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(activity!!,etToDate)
    }


    override fun onClickList() {
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","business_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onRemoveLoan(position: Int, loanHistoryBusiness: ResLoanHistoryBusiness.LoanHistory)
    {
        val dialog = AlertDialog.Builder(activity!!)

        val font = ResourcesCompat.getFont(activity!!, R.font.montserrat)
        val taskEditText    = CommonEditTextRegular(activity)
        taskEditText.typeface=font
        val textInputLayout = TextInputLayout(activity)

        val container =  FrameLayout(activity!!)
        val  params   =  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        params.setMargins(resources.getDimensionPixelOffset(R.dimen._15sdp), 0, resources.getDimensionPixelOffset(R.dimen._15sdp), 0)
        textInputLayout.layoutParams = params
        textInputLayout.addView(taskEditText)
        container.addView(textInputLayout)

        dialog.setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.Reason_for_cancellation))
                .setView(container)
                .setPositiveButton("Remove") { dialog, which ->

                    if(TextUtils.isEmpty(taskEditText.text.toString()))
                    {
                        CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_cancellation_reason))
                    }
                    else
                    {
                        if(CommonMethod.isNetworkAvailable(activity!!))
                        {
                            dialog.dismiss()
                            progressDialog.show()

                            val jsonObject = JsonObject()
                            jsonObject.addProperty("loan_type","business_loan")
                            jsonObject.addProperty("loan_id",loanHistoryBusiness.loan_id.toString())
                            jsonObject.addProperty("cancel_reason",taskEditText.text.toString())

                            val presenterLoanHistory=PresenterLoanHistory()
                            presenterLoanHistory.cancelLoanHistoryBusiness(activity!!,jsonObject,this,position)
                        }
                        else
                        {
                            dialog.dismiss()
                            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                        }
                    }


                }
                .setNegativeButton("Cancel", null)
                .create()

        dialog.show()

    }

    override fun onSuccessRemoveLoan(position: Int) {
        dismissDialog()
        listLoanHistoryBusiness!!.removeAt(position)
        adapterLoanHistory.notifyItemRemoved(position)
    }

    override fun onFailureRemoveLoan(message: String) {
        dismissDialog()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

}