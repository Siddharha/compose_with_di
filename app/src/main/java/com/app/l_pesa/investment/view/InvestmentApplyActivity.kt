package com.app.l_pesa.investment.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.investment.adapter.LoanPlanListAdapter
import com.app.l_pesa.investment.inter.ICallBackLoanPlanList
import com.app.l_pesa.investment.model.ResInvestmentPlan
import com.app.l_pesa.investment.presenter.PresenterApplyInvestment
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_investment_apply.*
import kotlinx.android.synthetic.main.content_investment_apply.*

class InvestmentApplyActivity : AppCompatActivity(), ICallBackLoanPlanList {

    private var investmentPlanId=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_investment_apply)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@InvestmentApplyActivity)
        swipeRefresh()
        initData()

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
        }
    }

    @SuppressLint("SetTextI18n")
    fun initData()
    {
        val globalInvestmentPlan= GlobalInvestmentPlanData.getInstance().modelData
        val planName=globalInvestmentPlan!!.planName
        etPlan.setText(planName)
        investmentPlanId=globalInvestmentPlan.investmentId

        etPlan.setOnClickListener {
            showPlan()
        }

        buttonDeposit.setOnClickListener {

            if(investmentPlanId==0)
            {
                showPlan()
                CommonMethod.customSnackBarError(rootLayout,this@InvestmentApplyActivity,resources.getString(R.string.select_investment_plan))
            }
            else if(TextUtils.isEmpty(etAmount.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,this@InvestmentApplyActivity,resources.getString(R.string.required_investment_amount))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(this@InvestmentApplyActivity))
                {
                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                    swipeRefreshLayout.isRefreshing=true
                    buttonDeposit.isClickable=false

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("deposit_id",investmentPlanId.toString())
                    jsonObject.addProperty("amount",etAmount.text.toString())

                    val presenterApplyInvestment= PresenterApplyInvestment()
                    presenterApplyInvestment.applyInvestment(this@InvestmentApplyActivity,this,jsonObject)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@InvestmentApplyActivity,resources.getString(R.string.no_internet))
                }
            }
        }

        val sharedPrefOBJ= SharedPref(this@InvestmentApplyActivity)
        txtContent.text=resources.getString(R.string.apply_an_investment_note_start)+" "+sharedPrefOBJ.investRateMin+"% up to "+sharedPrefOBJ.investRateMax+"% "+resources.getText(R.string.apply_an_investment_note_start)
    }

    override fun onSuccessApplyInvestment() {
        swipeRefreshLayout.isRefreshing=false
        buttonDeposit.isClickable=true
        onBackPressed()
    }

    override fun onErrorApplyInvestment(jsonMessage: String) {
        swipeRefreshLayout.isRefreshing=false
        buttonDeposit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,this@InvestmentApplyActivity,jsonMessage)
    }

    override fun onSelectLoan(planId: Int, planName: String) {

        etPlan.setText(planName)
        investmentPlanId=planId
    }

    private fun showPlan()
    {
        val sharedPrefOBJ= SharedPref(this@InvestmentApplyActivity)
        val loanPlanData = Gson().fromJson<ResInvestmentPlan.Data>( sharedPrefOBJ.loanPlanList, ResInvestmentPlan.Data::class.java)

        val dialog= Dialog(this@InvestmentApplyActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_id_type)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val loanPlanAdapter             = LoanPlanListAdapter(this@InvestmentApplyActivity, loanPlanData.investmentPlans!!,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@InvestmentApplyActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = loanPlanAdapter
        dialog.show()

    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                CommonMethod.hideKeyboardView(this@InvestmentApplyActivity)
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

}
