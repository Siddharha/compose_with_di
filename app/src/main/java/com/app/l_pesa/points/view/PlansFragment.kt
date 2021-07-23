package com.app.l_pesa.points.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.common.toast
import com.app.l_pesa.points.ICallBackCreditPlan
import com.app.l_pesa.points.adapters.CreditPlanListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.bottom_sheet_apply_credit_plan.view.*
import kotlinx.android.synthetic.main.fragment_plans.view.*

class PlansFragment : Fragment(), ICallBackCreditPlan {

    lateinit var rootView:View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val pref:SharedPref by lazy { SharedPref(requireContext()) }
    private val planLayoutManager:LinearLayoutManager by lazy { LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false) }
    private val planList:ArrayList<CreditPlanResponse.Data.Plan> by lazy { ArrayList() }
    private val creditPlanListAdapter:CreditPlanListAdapter by lazy { CreditPlanListAdapter(requireContext(),planList,this) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_plans, container, false)
        initialize()
        onActionPerform()
        getAllPlans()

        return rootView
    }

    private fun onActionPerform() {
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        rootView.flLayer.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED ->{
                        rootView.flLayer.visibility = View.GONE
                    }
                    else -> "Persistent Bottom Sheet"
                }
            }
        })
    }

    private fun initialize(){
        bottomSheetBehavior = BottomSheetBehavior.from(rootView.clApplyPlan)
        rootView.rvPlans.apply {
            layoutManager = planLayoutManager
            adapter = creditPlanListAdapter
        }

    }

    @SuppressLint("CheckResult")
    private fun getAllPlans() {
        RetrofitHelper.getRetrofitToken(BaseService::class.java,pref.accessToken).getAllCreditPlans()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody

                }
                .subscribe { response ->
                    if(response.status.isSuccess){
                        planList.clear()
                        planList.addAll(response.data.plans)
                        creditPlanListAdapter.notifyDataChanged()
                    }
                    //rootView.tvDisplay.text = response.toString()

                }
    }

    override fun onCreditPlanClickList() {

    }

    override fun onCreditPlanItemClickList(itm: CreditPlanResponse.Data.Plan) {
        val state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.state = state
        rootView.apply {
            tvTitle.text = itm.pointTitle
           tvDesc.text = itm.description
            tvPrice.text = itm.price
            tvScore.text = itm.creditScore.toString()
        }

    }


}