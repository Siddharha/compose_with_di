package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.lpk.model.ResInterestHistory

class AdapterInterestHistory (val context: Context, private val listInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.txtTokenValue.text = context.getString(R.string.actual_token_values)+":"+listInterestHistory[position].tokens
        viewHolder.txtAmount.text = listInterestHistory[position].currencyCode+" "+listInterestHistory[position].amount
        viewHolder.txtNarration.text = context.getString(R.string.narration)+":"+listInterestHistory[position].narration
        viewHolder.txtRef.text = context.getString(R.string.ref_no)+listInterestHistory[position].identityNumber

    }

    override fun getItemCount(): Int = listInterestHistory.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_interest_history, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

           var txtTokenValue          : CommonTextRegular = itemView.findViewById(R.id.txtTokenValue) as CommonTextRegular
           var txtAmount              : CommonTextRegular = itemView.findViewById(R.id.txtAmount) as CommonTextRegular
           var txtNarration           : CommonTextRegular = itemView.findViewById(R.id.txtNarration) as CommonTextRegular
           var txtRef                 : CommonTextRegular = itemView.findViewById(R.id.txtRef) as CommonTextRegular


        }

    }

}