package com.app.l_pesa.loanplan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.loanplan.model.ResLoanPlans

/**
 * Created by Intellij Amiya on 21/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

class CurrentLoanPlanAdapter (val context: Context, private val loanHistoryList: ArrayList<ResLoanPlans.Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        viewHolder.txtRequiredScore.text    = context.resources.getString(R.string.credit_score)+" "+loanHistoryList[position].details.requiredCreditScore.toString()
        viewHolder.txtLoanAmount.text       = "$"+loanHistoryList[position].details.loanAmount.toString()

        if(loanHistoryList[position].details.loanPeriodType=="D")
        {
            viewHolder.txtDuration.text    = context.resources.getString(R.string.duration)+" "+loanHistoryList[position].details.loanPeriod+" "+context.resources.getString(R.string.days)
        }
        else
        {
            viewHolder.txtDuration.text    = context.resources.getString(R.string.duration)+" "+loanHistoryList[position].details.loanPeriod+" "+context.resources.getString(R.string.weeks)

        }

        viewHolder.txtRate.text            = context.resources.getString(R.string.interest_rate)+" "+loanHistoryList[position].details.loanInterestRate.toString()+"%"

    }

    override fun getItemCount(): Int = loanHistoryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_loan_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

           var txtLoanAmount   : TextView = itemView.findViewById(R.id.txtLoanAmount) as TextView
           var txtRequiredScore: TextView = itemView.findViewById(R.id.txtRequiredScore) as TextView
           var txtDuration     : TextView = itemView.findViewById(R.id.txtDuration) as TextView
           var txtRate         : TextView = itemView.findViewById(R.id.txtRate) as TextView


        }


    }
}