package com.app.l_pesa.points.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.common.toast
import com.app.l_pesa.points.ICallBackCreditPlan
import com.app.l_pesa.points.adapters.CreditPlanListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_plans.view.*

class PlansFragment : Fragment(), ICallBackCreditPlan {

    lateinit var rootView:View
    private val pref:SharedPref by lazy { SharedPref(requireContext()) }
    private val planLayoutManager:LinearLayoutManager by lazy { LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false) }
    private val planList:ArrayList<CreditPlanResponse.Data.Plan> by lazy { ArrayList() }
    private val creditPlanListAdapter:CreditPlanListAdapter by lazy { CreditPlanListAdapter(requireContext(),planList,this) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_plans, container, false)
        initialize()
        getAllPlans()

        return rootView
    }

    private fun initialize(){
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


}