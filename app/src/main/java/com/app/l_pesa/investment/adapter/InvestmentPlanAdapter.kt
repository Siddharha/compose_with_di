package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.investment.model.ResInvestmentPlan

class InvestmentPlanAdapter (val context: Context, private val investmentList: ArrayList<ResInvestmentPlan.InvestmentPlan>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        viewHolder.txtInterest.text = investmentList[position].depositInterestRate.toString()+"%"
        viewHolder.txtTitle.text = investmentList[position].planName
        viewHolder.txtDuration.text = context.resources.getString(R.string.months)+"" +
                ""+investmentList[position].depositMonth.toString()

        viewHolder.txtRate.text = context.resources.getString(R.string.interest_rate)+"" +
                ""+investmentList[position].depositInterestRate+"%"
    }

    override fun getItemCount(): Int = investmentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_investment_plan_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtInterest          : TextView              = itemView.findViewById(R.id.txtInterest) as TextView
            var txtTitle             : TextView              = itemView.findViewById(R.id.txtTitle) as TextView
            var txtDuration          : TextView              = itemView.findViewById(R.id.txtDuration) as TextView
            var txtRate              : TextView              = itemView.findViewById(R.id.txtRate) as TextView



        }


    }
}