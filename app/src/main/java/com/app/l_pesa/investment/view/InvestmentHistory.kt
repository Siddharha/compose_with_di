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
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.investment.adapter.AdapterWindowInvestmentHistory
import com.app.l_pesa.investment.adapter.InvestmentHistoryAdapter
import com.app.l_pesa.investment.inter.ICallBackEditHistory
import com.app.l_pesa.investment.inter.ICallBackInvestmentHistory
import com.app.l_pesa.investment.inter.ICallBackPopUpWindow
import com.app.l_pesa.investment.model.ModelWindowHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import com.app.l_pesa.investment.presenter.PresenterInvestmentExitPoint
import com.app.l_pesa.investment.presenter.PresenterInvestmentHistory
import com.app.l_pesa.investment.presenter.PresenterInvestmentReinvestment
import com.app.l_pesa.investment.presenter.PresenterInvestmentWithdrawal
import com.app.l_pesa.lpk.inter.ICallBackInvestmentStatus
import com.app.l_pesa.lpk.presenter.PresenterInvestmentStatus
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_investment_history.*
import java.util.ArrayList

class InvestmentHistory:Fragment(),ICallBackInvestmentHistory, ICallBackEditHistory, ICallBackInvestmentStatus {


    private lateinit  var progressDialog: KProgressHUD
    private var popupWindow : PopupWindow? = null
    private var selectedItem: Int = -1

    private lateinit var listInvestment               : ArrayList<ResInvestmentHistory.UserInvestment>
    private lateinit var adapterInvestmentHistory     : InvestmentHistoryAdapter

    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return InvestmentHistory()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_investment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initUI()
        switchFunction()

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
        listInvestment             = ArrayList()
        adapterInvestmentHistory   = InvestmentHistoryAdapter(activity!!, listInvestment,this)
        initLoader()

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistory(activity!!,this)
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
            userDashBoard.savingInvestAutoStatus=1
            val gson = Gson()
            val json = gson.toJson(userDashBoard)
            sharedPrefOBJ.userDashBoard      = json

        }
        else
        {
            userDashBoard.savingInvestAutoStatus=0
            val gson = Gson()
            val json = gson.toJson(userDashBoard)
            sharedPrefOBJ.userDashBoard      = json
        }

    }

    override fun onErrorInvestmentStatus(message: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
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
        progressDialog= KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun dismissPopup() {
        popupWindow?.let {
            if(it.isShowing){
                it.dismiss()
            }
        }

    }

    override fun onSuccessInvestmentHistory(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?) {

        activity!!.runOnUiThread {
            hasNext =cursors!!.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            listInvestment.clear()
            listInvestment.addAll(userInvestment)
            adapterInvestmentHistory    = InvestmentHistoryAdapter(activity!!, listInvestment,this)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rvLoan.layoutManager        = llmOBJ
            rvLoan.adapter              = adapterInvestmentHistory

            adapterInvestmentHistory.setLoadMoreListener(object : InvestmentHistoryAdapter.OnLoadMoreListener {
                override fun onLoadMore() {

                    rvLoan.post {

                        if(hasNext)
                        {
                            loadMore()
                        }

                    }

                }
            })


        }

    }

    override fun onSuccessInvestmentHistoryPaginate(userInvestment: ArrayList<ResInvestmentHistory.UserInvestment>, cursors: ResInvestmentHistory.Cursors?) {

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

    private fun loadMore()
    {

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val actionStatusModel= ResInvestmentHistory.ActionState(false,false,false,"","")
            val loanStatusModel  = ResInvestmentHistory.UserInvestment(0, 0,0,0,"","","","",
                                   "","","","",0.0,0.0,0.0,0.0,actionStatusModel)

            listInvestment.add(loanStatusModel)
            adapterInvestmentHistory.notifyItemInserted(listInvestment.size-1)

            val presenterInvestmentHistory= PresenterInvestmentHistory()
            presenterInvestmentHistory.getInvestmentHistoryPaginate(activity!!,after,this)

        }

    }


    override fun onEmptyInvestmentHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorInvestmentHistory(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
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

        val filterItemList = mutableListOf<ModelWindowHistory>()

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


        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val adapter = AdapterWindowInvestmentHistory(activity!!)
        adapter.addAlertFilter(filterItemList)
        recyclerView.adapter = adapter
        adapter.selectedItem(selectedItem)

        adapter.setOnClick(object : ICallBackPopUpWindow {
            override fun onItemClick(view: View, position: Int, modelWindowHistory: String) {
                selectedItem = position

                if(investmentList.actionState.btnExitPointStatus=="disable" && modelWindowHistory==resources.getString(R.string.exit_point))
                {
                  Toast.makeText(activity,investmentList.actionState.btnExitPointStatusMessage,Toast.LENGTH_SHORT).show()
                }
                else if(investmentList.actionState.btnExitPointStatus=="enable" && modelWindowHistory==resources.getString(R.string.exit_point))
                {
                    if(CommonMethod.isNetworkAvailable(activity!!))
                    {

                        investmentExitPoint(investmentList.investment_id.toString())
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

                     investmentWithdrawal(investmentList.investment_id.toString())
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

                        investmentReinvest(investmentList.investment_id.toString())
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                    }
                }
                dismissPopup()
            }
        })

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

        initUI()
    }

    override fun onErrorInvestmentWithdrawal(message: String) {

        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSuccessReinvestment() {
        initUI()
    }

    override fun onErrorReinvestment(message: String) {
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSuccessExitPoint() {
        initUI()
    }

    override fun onErrorExitPoint(message: String) {
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}