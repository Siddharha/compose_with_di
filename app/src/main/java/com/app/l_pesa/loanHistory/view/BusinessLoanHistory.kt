package com.app.l_pesa.loanHistory.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.*
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.adapter.BusinessLoanHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackBusinessLoanHistory
import com.app.l_pesa.loanHistory.model.ResLoanHistoryBusiness
import com.app.l_pesa.loanHistory.presenter.PresenterLoanHistory
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_history_list.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*


class BusinessLoanHistory: Fragment(), ICallBackBusinessLoanHistory {

    private lateinit  var progressDialog        : ProgressDialog
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
        progressDialog = ProgressDialog(activity!!,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

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
            val logger = AppEventsLogger.newLogger(activity)
            val params =  Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Business Loan History")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

            swipeRefreshLayout.isRefreshing = true
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","business_loan")
            val presenterLoanHistory= PresenterLoanHistory()
            presenterLoanHistory.getLoanHistoryBusiness(activity!!,jsonObject,from_date,to_date,type,"",this)

        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            swipeRefreshLayout.isRefreshing = false

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
            llmOBJ.orientation          = RecyclerView.VERTICAL
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
        CommonMethod.datePicker(activity!!,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        CommonMethod.datePicker(activity!!,etToDate)
    }


    override fun onClickList() {

        val logger = AppEventsLogger.newLogger(activity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Business Loan History")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","business_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onRemoveLoan(position: Int, loanHistoryBusiness: ResLoanHistoryBusiness.LoanHistory)
    {
        val dialog = AlertDialog.Builder(requireContext())

        val font = ResourcesCompat.getFont(activity!!, R.font.montserrat)
        val taskEditText    = CommonEditTextRegular(activity!!)
        taskEditText.typeface=font
        val textInputLayout = TextInputLayout(activity!!)

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

    override fun onSessionTimeOut(message: String) {
        swipeRefreshLayout.isRefreshing = false
        val dialogBuilder = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
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

}