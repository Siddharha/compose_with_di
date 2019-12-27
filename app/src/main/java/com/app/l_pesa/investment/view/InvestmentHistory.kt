package com.app.l_pesa.investment.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypefaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.investment.adapter.AdapterWindowInvestmentHistory
import com.app.l_pesa.investment.adapter.InvestmentHistoryAdapter
import com.app.l_pesa.investment.inter.ICallBackEditHistory
import com.app.l_pesa.investment.inter.ICallBackInvestmentHistory
import com.app.l_pesa.investment.inter.ICallBackPopUpWindow
import com.app.l_pesa.investment.model.ModelWindowHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import com.app.l_pesa.investment.presenter.*
import com.app.l_pesa.lpk.inter.ICallBackInvestmentStatus
import com.app.l_pesa.lpk.presenter.PresenterInvestmentStatus
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_investment_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*

class InvestmentHistory: androidx.fragment.app.Fragment(),ICallBackInvestmentHistory, ICallBackEditHistory, ICallBackInvestmentStatus,ICallBackPopUpWindow {


    private lateinit  var progressDialog: ProgressDialog
    private var popupWindow : PopupWindow? = null
    private lateinit var listInvestment               : ArrayList<ResInvestmentHistory.UserInvestment>
    private lateinit var adapterInvestmentHistory     : InvestmentHistoryAdapter
    private lateinit var bottomSheetBehavior          : BottomSheetBehavior<*>

    private var hasNext =false
    private var after=""

    companion object {
        fun newInstance(): androidx.fragment.app.Fragment {
            return InvestmentHistory()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_investment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initData("","","DEFAULT")
        switchFunction()

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

    private fun initData(from_date: String, to_date: String,type:String)
    {
        listInvestment               = ArrayList()
        adapterInvestmentHistory     = InvestmentHistoryAdapter(activity!!, listInvestment,this)
        bottomSheetBehavior          = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        initLoader()

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val logger = AppEventsLogger.newLogger(activity)
            val params =  Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Investment History")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

            swipeRefreshLayout.isRefreshing = true
            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistory(activity!!,from_date,to_date,type,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun switchFunction()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        switchInvestment.isChecked = userDashBoard!!.savingInvestAutoStatus==1

        switchInvestment.setOnCheckedChangeListener { _, isChecked -> run {

                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    progressDialog.show()
                    val jsonObject = JsonObject()
                    if(isChecked)
                    {
                        jsonObject.addProperty("steady_income_status","1")
                    }
                    else
                    {
                        jsonObject.addProperty("steady_income_status","0")
                    }

                    val presenterInvestmentStatus= PresenterInvestmentStatus()
                    presenterInvestmentStatus.doInvestmentStatus(activity!!,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }
        }
    }

    override fun onSuccessInvestmentStatus() {
        dismiss()
        val sharedPrefOBJ= SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        if(switchInvestment.isChecked)
        {
            userDashBoard.savingInvestAutoStatus = 1
            val json = Gson().toJson(userDashBoard)
            sharedPrefOBJ.userDashBoard      = json

        }
        else
        {
            userDashBoard.savingInvestAutoStatus = 0
            val json = Gson().toJson(userDashBoard)
            sharedPrefOBJ.userDashBoard      = json
        }

    }

    override fun onErrorInvestmentStatus(message: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSessionTimeOut(message: String) {
        dismiss()
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

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(activity!!,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismissPopup() {
        popupWindow?.let {
            if(it.isShowing){
                it.dismiss()
            }
        }

    }

    override fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?, from_date: String, to_date: String) {

        activity!!.runOnUiThread {

            cardView.visibility=View.INVISIBLE
            rvLoan.visibility=View.VISIBLE

            hasNext =cursors!!.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            listInvestment.clear()
            listInvestment.addAll(userInvestment)
            adapterInvestmentHistory    = InvestmentHistoryAdapter(activity!!, listInvestment,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = RecyclerView.VERTICAL
            rvLoan.layoutManager        = llmOBJ
            rvLoan.adapter              = adapterInvestmentHistory

            adapterInvestmentHistory.setLoadMoreListener(object : InvestmentHistoryAdapter.OnLoadMoreListener {
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

    override fun onSuccessInvestmentHistoryPaginate(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?,from_date: String, to_date: String) {

        swipeRefreshLayout.isRefreshing    = false
        hasNext =cursors!!.hasNext
        after   =cursors.after
        if(listInvestment.size!=0)
        {
            try {

                listInvestment.removeAt(listInvestment.size - 1)
                adapterInvestmentHistory.notifyDataChanged()
                listInvestment.addAll(userInvestment)
                adapterInvestmentHistory.notifyItemRangeInserted(0, listInvestment.size)

            }
            catch (e:Exception)
            {}
        }
    }

    private fun loadMore(from_date: String, to_date: String)
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val actionStatusModel= ResInvestmentHistory.ActionState(false,false,false,"","")
            val loanStatusModel  = ResInvestmentHistory.UserInvestment(0, 0,0,0,"","","","",
                                   "","","","","",0.0,0.0,0.0,0.0,actionStatusModel)

            listInvestment.add(loanStatusModel)
            adapterInvestmentHistory.notifyItemInserted(listInvestment.size-1)

            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistoryPaginate(activity!!,after,from_date,to_date,this)

        }

    }


    override fun onEmptyInvestmentHistory(type: String) {

        swipeRefreshLayout.isRefreshing = false
        rvLoan.visibility=View.INVISIBLE
        if(type=="FILTER")
        {
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        else
        {
            txt_message.text = resources.getString(R.string.empty_investment_message)
        }
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorInvestmentHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        rvLoan.visibility=View.INVISIBLE
        cardView.visibility=View.INVISIBLE
        Toast.makeText(activity,jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onEditWindow(imgEdit: ImageButton, investmentList: ResInvestmentHistory.UserInvestment) {

        dismissPopup()
        popupWindow = showAlertFilter(investmentList)
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.isFocusable = true
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       // filterPopup?.showAsDropDown(rootLayout,150,-420)
        popupWindow!!.showAsDropDown(imgEdit)
    }


    @SuppressLint("InflateParams")
    private fun showAlertFilter(investmentList: ResInvestmentHistory.UserInvestment): PopupWindow {

        val filterItemList = ArrayList<ModelWindowHistory>()

        if(investmentList.actionState.btnWithdrawalShow)
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.withdrawal),investmentList.actionState.btnWithdrawalShow))
        }
        if(investmentList.actionState.btnReinvestShow)
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.reinvestment),investmentList.actionState.btnReinvestShow))
        }
        if(investmentList.actionState.btnExitPointShow)
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.exit_point),investmentList.actionState.btnExitPointShow))
        }
        if(investmentList.deposit_status=="P")
        {
            filterItemList.add(ModelWindowHistory(resources.getString(R.string.delete),true))
        }


        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(recyclerView.context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))

        val adapter = AdapterWindowInvestmentHistory(activity!!,filterItemList,investmentList,this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun investmentWithdrawal(investment_id: String)
    {
        swipeRefreshLayout.isRefreshing = true
        val jsonObject = JsonObject()
        jsonObject.addProperty("deposit_id",investment_id)
        //println("JSON___W"+jsonObject.toString())
        val presenterInvestmentWithdrawal= PresenterInvestmentWithdrawal()
        presenterInvestmentWithdrawal.doInvestmentWithdrawal(activity!!,this,jsonObject)
    }

    private fun investmentReinvest(investment_id: String)
    {
        swipeRefreshLayout.isRefreshing = true
        val jsonObject = JsonObject()
        jsonObject.addProperty("deposit_id",investment_id)
        //println("JSON___R"+jsonObject.toString())
        val presenterInvestmentReinvestment= PresenterInvestmentReinvestment()
        presenterInvestmentReinvestment.doReinvestment(activity!!,this,jsonObject)
    }

    private fun investmentExitPoint(investment_id: String)
    {
        swipeRefreshLayout.isRefreshing = true
        val jsonObject = JsonObject()
        jsonObject.addProperty("deposit_id",investment_id)
        //println("JSON___E"+jsonObject.toString())
        val presenterInvestmentExitPoint= PresenterInvestmentExitPoint()
        presenterInvestmentExitPoint.doInvestmentExitPoint(activity!!,this,jsonObject)
    }

    override fun onSuccessInvestmentWithdrawal() {

        initData("","","DEFAULT")
    }

    override fun onErrorInvestmentWithdrawal(message: String) {

        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSuccessReinvestment() {
        initData("","","DEFAULT")
    }

    override fun onErrorReinvestment(message: String) {
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSuccessExitPoint() {
        initData("","","DEFAULT")
    }

    override fun onErrorExitPoint(message: String) {
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onItemClick(view: View, position: Int, modelWindowHistory: String, investmentList: ResInvestmentHistory.UserInvestment) {

        if(investmentList.actionState.btnExitPointStatus=="disable" && modelWindowHistory==resources.getString(R.string.exit_point))
        {
            Toast.makeText(activity,investmentList.actionState.btnExitPointStatusMessage,Toast.LENGTH_SHORT).show()
        }
        else if(investmentList.actionState.btnExitPointStatus=="enable" && modelWindowHistory==resources.getString(R.string.exit_point))
        {
            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                val alertDialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.apply_exit_point))
                alertDialog.setPositiveButton("Yes") { _, _ -> investmentExitPoint(investmentList.investment_id.toString()) }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()


            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }
        }
        else if(modelWindowHistory==resources.getString(R.string.withdrawal))
        {
            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                val alertDialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.apply_withdrawal))
                alertDialog.setPositiveButton("Yes") { _, _ -> investmentWithdrawal(investmentList.investment_id.toString()) }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()


            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }
        }
        else if(modelWindowHistory==resources.getString(R.string.reinvestment))
        {
            if(CommonMethod.isNetworkAvailable(activity!!))
            {

                val alertDialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.apply_reinvestment))
                alertDialog.setPositiveButton("Yes") { _, _ ->  investmentReinvest(investmentList.investment_id.toString()) }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }
        }

        else if(modelWindowHistory==resources.getString(R.string.delete))
        {
            if(CommonMethod.isNetworkAvailable(activity!!))
            {

                val alertDialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.delete_exit_loan))
                alertDialog.setPositiveButton("Yes") { _, _ ->

                    doRemoveInvest(investmentList.investment_id,position)
                }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }
        }
        dismissPopup()

    }

    private fun doRemoveInvest(investment_id: Int, position: Int)
    {
        progressDialog.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("deposit_id",investment_id.toString())
        val presenterApplyInvestment= PresenterApplyInvestment()
        presenterApplyInvestment.removeInvestment(activity!!,this,jsonObject,position)
    }

    override fun onSuccessRemoveInvestment(position: Int) {
        dismiss()
        listInvestment.removeAt(position)
        adapterInvestmentHistory.notifyItemRemoved(position)
    }

    override fun onErrorRemoveInvestment(message: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
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

            if (TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString())) {
                CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.you_have_select_from_date_to_date))
            } else {

                val fromDate = CommonMethod.dateConvertYMD(etFromDate.text.toString())
                val toDate = CommonMethod.dateConvertYMD(etToDate.text.toString())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                initData(fromDate!!,toDate!!,"FILTER")

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

    private fun resetFilter()
    {
        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
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
}