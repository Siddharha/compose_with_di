package com.app.l_pesa.investment.adapter

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.investment.inter.ICallBackLoanPlanList
import com.app.l_pesa.investment.model.ResInvestmentPlan

class LoanPlanListAdapter (val context: Context, private val titleText: ArrayList<ResInvestmentPlan.InvestmentPlan>, private val dialogOBJ: Dialog, private val callBack: ICallBackLoanPlanList) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.titleText.text = titleText[position].planName
        viewHolder.rlRootObj.setOnClickListener {
            dialogOBJ.dismiss()
            callBack.onSelectLoan(titleText[position].investmentId,titleText[position].planName)
        }


    }

    override fun getItemCount(): Int = titleText.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_marital, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var titleText : TextView = itemView.findViewById(R.id.txtTitle) as TextView
            var rlRootObj : RelativeLayout = itemView.findViewById(R.id.rlRoot) as RelativeLayout

        }

    }

}