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
import com.app.l_pesa.loanplan.adapter.BusinessLoanPlanAdapter
import com.app.l_pesa.loanplan.inter.ICallBackBusinessLoan
import com.app.l_pesa.loanplan.model.GlobalLoanPlanModel
import com.app.l_pesa.loanplan.model.ResLoanPlans
import com.app.l_pesa.loanplan.presenter.PresenterLoanPlans
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_loan_plan_list.*
import java.util.*

class BusinessLoan:Fragment(), ICallBackBusinessLoan {

    companion object {
        fun newInstance(): Fragment {
            return BusinessLoan()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loanLoan()
        swipeRefresh()
    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            swipeRefreshLayout.isRefreshing = true
            loanLoan()
        }
    }

    private fun loanLoan()
    {
        val logger = AppEventsLogger.newLogger(activity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Business Loan")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        if(CommonMethod.isNetworkAvailable(requireActivity()))
        {
            shimmerLayout.startShimmer()
            val jsonObject = JsonObject()
            jsonObject.addProperty("loan_type","business_loan")
            val presenterLoanPlans= PresenterLoanPlans()
            presenterLoanPlans.doLoanPlansBusiness(requireActivity(),jsonObject,this)
        }
        else
        {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility=View.INVISIBLE
            CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.no_internet))
            swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onSuccessLoanPlans(item: ArrayList<ResLoanPlans.Item>, appliedProduct: ResLoanPlans.AppliedProduct?) {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility=View.INVISIBLE
        val sharedPref= SharedPref(requireActivity())
        sharedPref.businessLoanCount="1"
        (activity as DashboardActivity).isVisibleToolbarRight()
        cardView.visibility   = View.GONE
        rvLoan.visibility     = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
        val businessLoanAdapter  = BusinessLoanPlanAdapter(requireActivity(), item,appliedProduct!!,this)
        rvLoan.layoutManager     = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        rvLoan.adapter           = businessLoanAdapter
    }

    override fun onEmptyLoanPlans() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility=View.INVISIBLE
        val sharedPref= SharedPref(requireActivity())
        sharedPref.businessLoanCount="0"
        (activity as DashboardActivity).isVisibleToolbarRight()
        rvLoan.visibility   = View.GONE
        cardView.visibility = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onFailureLoanPlans(jsonMessage: String) {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility=View.INVISIBLE
        val sharedPref= SharedPref(requireActivity())
        sharedPref.businessLoanCount="0"
        (activity as DashboardActivity).isVisibleToolbarRight()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onSuccessLoanPlansDetails(details: ResLoanPlans.Details?) {

        val globalLoanPlanModel= GlobalLoanPlanModel.getInstance()
        globalLoanPlanModel.modelData=details
        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","business_loan")
        val intent = Intent(activity, LoanPlanDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onSuccessLoanHistory() {

        val bundle = Bundle()
        bundle.putString("LOAN_TYPE","business_loan")
        val intent = Intent(activity, LoanHistoryDetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onSessionTimeOut(message: String) {
        swipeRefreshLayout.isRefreshing = false
        val dialogBuilder = AlertDialog.Builder(requireActivity(),R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(requireActivity())
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    requireActivity().finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }
}