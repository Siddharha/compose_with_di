package com.app.l_pesa.points.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.points.ICallBackCreditPlan
import com.app.l_pesa.points.view.CreditPlanResponse
import kotlinx.android.synthetic.main.credit_plan_list_cell.view.*

class CreditPlanListAdapter (val context: Context, private val planlist: ArrayList<CreditPlanResponse.Data.Plan>, private val callBackCreditPlan: ICallBackCreditPlan) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {


   // private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)

        return CreditPlanViewHolder(inflater.inflate(R.layout.credit_plan_list_cell, parent, false))
//        return if (viewType == 0)
//        {
//            UserViewHolder(inflater.inflate(R.layout.layout_loan_history, parent, false))
//        } else {
//            LoadingViewHolder(inflater.inflate(R.layout.layout_load_more, parent, false))
//        }
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {
        (holder as CreditPlanViewHolder).bindData(context, planlist[position],callBackCreditPlan,position)
//        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading)
//        {
//            isLoading = true
//          //  loadMoreListener.onLoadMore()
//        }

//        if (getItemViewType(position) == 0) {
//            (holder as UserViewHolder).bindData(context, planlist[position],callBackCreditPlan,position)
//
//        }
    }

//    override fun getItemViewType(position: Int): Int
//    {
//        return if(planlist[position].!=0){
//            0
//        }else{
//            1
//        }
//    }

    override fun getItemCount(): Int {
        return planlist.size
    }


    fun notifyDataChanged() {
        notifyDataSetChanged()
        isLoading = false
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

//    internal fun setLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
//        this.loadMoreListener = loadMoreListener
//    }

    class CreditPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, plan: CreditPlanResponse.Data.Plan, iCallBackCreditPlan: ICallBackCreditPlan, position: Int) {

            itemView.apply{
                tvLoanPlanContent.text = plan.pointTitle
            setOnClickListener {
                iCallBackCreditPlan.onCreditPlanClickList()
            }
            }
        }

    }

    //class LoadingViewHolder(view: View) :RecyclerView.ViewHolder(view)

}