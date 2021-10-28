package com.app.l_pesa.loanplan.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.loanplan.inter.ICallBackTermsDescription
import com.app.l_pesa.loanplan.model.ResLoanTenure


class LoanTermsListAdapter (val context: Context, private val listTerms: List<ResLoanTenure.Data.Option>, private val dialogOBJ: Dialog, private val callBackTerms: ICallBackTermsDescription) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.titleText.text = "${listTerms[position].weeks} Weeks (${listTerms[position].days} Days)"
        //viewHolder.durationDateText.text = listTerms[position].default
        viewHolder.dueDateText.text = listTerms[position].due
        viewHolder.rlRootObj.setOnClickListener {
            dialogOBJ.dismiss()
            callBackTerms.onSelectTerms(listTerms[position])
        }


    }

    override fun getItemCount(): Int = listTerms.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_marital_loan_term_cell, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var titleText : TextView = itemView.findViewById(R.id.tvTitleLoanTerm) as TextView
            //var durationDateText : TextView = itemView.findViewById(R.id.tvDuration) as TextView
            var dueDateText : TextView = itemView.findViewById(R.id.tvTotalDueVal) as TextView
            var rlRootObj : View = itemView


        }

    }

}