package com.app.l_pesa.loanplan.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.view.LoanHistoryDetailsActivity
import com.app.l_pesa.loanplan.adapter.CurrentLoanPlanAdapter
import com.app.l_pesa.loanplan.inter.ICallBackCurrentLoan
import com.app.l_pesa.loanplan.model.GlobalLoanPlanModel
import com.app.l_pesa.loanplan.model.ResLoanPlans
import com.app.l_pesa.loanplan.presenter.PresenterLoanPlans
import com.app.l_pesa.main.view.MainActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import kotlinx.android.synthetic.main.fragment_loan_plan_list.shimmerLayout
import kotlinx.android.synthetic.main.fragment_loan_plan_list.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

class CurrentLoan: Fragment(), ICallBackCurrentLoan {


    companion object {
        fun newInstance(): Fragment {
            return CurrentLoan()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadLoan()
        swipeRefresh()
    }

    private fun loadLoan()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            shimmerLayout.startShimmerAnimation()
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","current_loan")
            val presenterLoanPlans= PresenterLoanPlans()
            presenterLoanPlans.doLoanPlans(activity!!,jsonObject,this)
        }
        else
        {
            shimmerLayout.stopShimmerAnimation()
            shimmerLayout.visibility=View.INVISIBLE
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

           loadLoan()
        }
    }

    override fun onSuccessLoanPlans(item: ArrayList<ResLoanPlans.Item>, appliedProduct: ResLoanPlans.AppliedProduct?) {

        shimmerLayout.stopShimmerAnimation()
        shimmerLayout.visibility=View.INVISIBLE
        val sharedPref=SharedPref(activity!!)
        sharedPref.currentLoanCount="1"
        (activity as DashboardActivity).isVisibleToolbarRight()
        cardView.visibility   = View.GONE
        rvLoan.visibility     = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
        val currentLoanAdapter   = CurrentLoanPlanAdapter(activity!!, item, appliedProduct!!, this)
        rvLoan.layoutManager     = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        rvLoan.adapter = currentLoanAdapter
    }

    override fun onEmptyLoanPlans() {
        shimmerLayout.stopShimmerAnimation()
        shimmerLayout.visibility=View.INVISIBLE
        val sharedPref=SharedPref(activity!!)
        sharedPref.currentLoanCount="0"
        (activity as DashboardActivity).isVisibleToolbarRight()
        rvLoan.visibility   = View.GONE
        cardView.visibility = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false

    }

    override fun onFailureLoanPlans(jsonMessage: String) {
        shimmerLayout.stopShimmerAnimation()
        shimmerLayout.visibility=View.INVISIBLE
        val sharedPref=SharedPref(activity!!)
        sharedPref.currentLoanCount="0"
        (activity as DashboardActivity).isVisibleToolbarRight()
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,jsonMessage)
    }

    override fun onSuccessLoanPlansDetails(details: ResLoanPlans.Details?) {

        val globalLoanPlanModel= GlobalLoanPlanModel.getInstance()
        globalLoanPlanModel.modelData=details

        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","current_loan")
        val intent = Intent(activity, LoanPlanDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onSuccessLoanHistory() {
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","current_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
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