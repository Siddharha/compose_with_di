package com.app.l_pesa.dashboard.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.adapter.LoanListAdapter
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.inter.ICallBackListOnClick
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.app.l_pesa.loanHistory.view.LoanPaybackScheduledActivity
import com.app.l_pesa.lpk.inter.ICallBackInfoLPK
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterInfoLPK
import com.app.l_pesa.lpk.view.LPKSavingsActivity
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.profile.view.ProfileFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dashboard_layout.*
import java.text.DecimalFormat


class DashboardFragment: androidx.fragment.app.Fragment(), ICallBackDashboard, ICallBackListOnClick, ICallBackInfoLPK {


   private lateinit  var progressDialog: ProgressDialog

   companion object {
        fun newInstance(): androidx.fragment.app.Fragment {
            return DashboardFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_dashboard_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initUI()
        initData()
        onActionPerform()
        loadDashboard()
    }

    private fun onActionPerform() {
        btnProfile.setOnClickListener {
            (context as DashboardActivity).gotoCompleteProfile()
        }
    }

    private fun initUI() {
        Handler().postDelayed({
            (activity as DashboardActivity).visibleFilter(false)
            (activity as DashboardActivity).visibleButton(false)
        }, 200)
        initSeekBar()
    }

    private fun initSeekBar() {
        seekBar.setOnTouchListener { _, _ -> true }

    }

    private fun swipeRefresh() {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            loadDashboard()
        }
    }

   private fun initData() {

        initLoader()
        val sharedPrefOBJ = SharedPref(activity!!)
        val dashBoard = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)

        if (dashBoard != null) {
            setDashBoard(dashBoard)
        }


    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(activity!!,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun loadDashboard() {
        if (CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val sharedPrefOBJ = SharedPref(activity!!)

            val presenterDashboard = PresenterDashboard()
            presenterDashboard.getDashboard(activity!!, sharedPrefOBJ.accessToken, this)
        } else {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.no_internet))
        }
    }

    private fun setDashBoard(dashBoard: ResDashboard.Data) {

        setData(dashBoard)
    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {
        swipeRefreshLayout.isRefreshing = false
        setData(data)
    }

    override fun onFailureDashboard(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
    }


    @SuppressLint("SetTextI18n")
    private fun setData(dashBoard: ResDashboard.Data) {
        println("max credit score : ${dashBoard.maxCreditScore.toFloat()}")
        println("credit score : ${dashBoard.creditScore.toFloat()}")
        println("profileCompletePercentage: ${dashBoard.profileCompletePercentage}")

        if(dashBoard.profileCompletePercentage!!<100){
            llProfileComp.visibility = View.VISIBLE
            pbProfile.progress = dashBoard.profileCompletePercentage!!
            tvProgress.text = "${dashBoard.profileCompletePercentage} %"
        }else{
            llProfileComp.visibility = View.GONE
        }

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        left_header_txt.text  = dashBoard.currencyCode+" "+format.format(dashBoard.fixedDepositAmount)+" "+resources.getString(R.string.in_deposit)
        right_header_txt.text = dashBoard.currencyCode+" "+format.format(dashBoard.savingsAmount)+" "+resources.getString(R.string.in_wallet)

        txt_start.text          = dashBoard.minCreditScore.toString()
        txt_max.text            = dashBoard.maxCreditScore.toString()

        seekBar.post{

            seekBar.max = dashBoard.maxCreditScore.toFloat()
            seekBar.setProgress(dashBoard.creditScore.toFloat())

        }

            if (dashBoard.loans!!.size > 0) {
                loan_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                val adapterDashBoard = LoanListAdapter(dashBoard.loans!!,dashBoard, activity, rootLayout, this)
                loan_list.adapter = adapterDashBoard
                adapterDashBoard.notifyDataSetChanged()
            }

            val sharedPrefOBJ = SharedPref(activity!!)
            val gson = Gson()
            val dashBoardData = gson.toJson(dashBoard)
            sharedPrefOBJ.userDashBoard = dashBoardData

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    override fun onClickLoanList(type: String) {

        if(type=="LPK")
        {
            progressDialog.show()
            val presenterInfoLPK= PresenterInfoLPK()
            presenterInfoLPK.getInfoLPK(activity!!,this,"SAVINGS")

        }
        else
        {
            val sharedPref = SharedPref(activity!!)
            sharedPref.navigationTab = resources.getString(R.string.open_tab_loan)
            sharedPref.openTabLoan = type

            val intent = Intent(activity, DashboardActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }


    }

    override fun onSuccessInfoLPK(data: ResInfoLPK.Data?, type: String) {

        val sharedPrefOBJ= SharedPref(activity!!)
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPrefOBJ.lpkInfo= json
        dismiss()
        startActivity(Intent(activity, LPKSavingsActivity::class.java))
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)


    }

    override fun onErrorInfoLPK(message: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

   override fun onSessionTimeOut(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        dismiss()
        val dialogBuilder = AlertDialog.Builder(activity!!, R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(activity!!)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    activity!!.finish()
                }

       val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }

    override fun onClickPay(type: String, loan_id: String) {

        val sharedPref = SharedPref(activity!!)
        sharedPref.payFullAmount = "A"
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE", type)
        bundle.putString("LOAN_ID", loan_id)
        val intent = Intent(activity, LoanPaybackScheduledActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent, bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }


}




