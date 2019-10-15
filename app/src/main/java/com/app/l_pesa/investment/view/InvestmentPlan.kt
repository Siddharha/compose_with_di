package com.app.l_pesa.investment.view


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.investment.adapter.InvestmentPlanAdapter
import com.app.l_pesa.investment.inter.ICallBackInvestmentPlan
import com.app.l_pesa.investment.model.ResInvestmentPlan
import com.app.l_pesa.investment.presenter.PresenterInvestmentPlan
import com.app.l_pesa.main.view.MainActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_investment_plan_list.*
import java.text.DecimalFormat


class InvestmentPlan: Fragment(), ICallBackInvestmentPlan {


    companion object {
        fun newInstance(): Fragment {
            return InvestmentPlan()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_investment_plan_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        investmentPlan()
        swipeRefresh()

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            investmentPlan()
        }
    }

    private fun investmentPlan()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            shimmerLayout.startShimmerAnimation()
            val presenterLoanPlans= PresenterInvestmentPlan()
            presenterLoanPlans.getInvestmentPlan(activity!!,this)
        }
        else
        {
            shimmerLayout.stopShimmerAnimation()
            shimmerLayout.visibility=View.INVISIBLE
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onSuccessInvestmentPlan(data: ResInvestmentPlan.Data) {

        shimmerLayout.stopShimmerAnimation()
        shimmerLayout.visibility=View.INVISIBLE
        cardView.visibility=View.GONE
        val sharedPrefOBJ= SharedPref(activity!!)
        val json = Gson().toJson(data)
        sharedPrefOBJ.loanPlanList  =json

        swipeRefreshLayout.isRefreshing    = false
        val investmentPlanAdapter          = InvestmentPlanAdapter(activity!!, data.investmentPlans!!,this)
        rvInvestment.layoutManager               = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        rvInvestment.adapter                     = investmentPlanAdapter

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        sharedPrefOBJ.investRateMin=format.format(data.investmentPlans!![0].depositInterestRate)
        sharedPrefOBJ.investRateMax=format.format(data.investmentPlans!![data.investmentPlans!!.size-1].depositInterestRate)
    }

    override fun onEmptyInvestmentPlan() {

       shimmerLayout.stopShimmerAnimation()
       shimmerLayout.visibility=View.INVISIBLE
       swipeRefreshLayout.isRefreshing = false
       cardView.visibility=View.VISIBLE
    }

    override fun onErrorInvestmentPlan(jsonMessage: String) {

        shimmerLayout.stopShimmerAnimation()
        shimmerLayout.visibility=View.INVISIBLE
        cardView.visibility=View.GONE
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity,jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onClickInvestmentPlan(investmentPlan: ResInvestmentPlan.InvestmentPlan) {

        val globalInvestmentPlan= GlobalInvestmentPlanData.getInstance()
        globalInvestmentPlan.modelData=investmentPlan

        startActivity(Intent(activity, InvestmentApplyActivity::class.java))
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onSessionTimeOut(jsonMessage: String) {
        swipeRefreshLayout.isRefreshing = false
        val dialogBuilder = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
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