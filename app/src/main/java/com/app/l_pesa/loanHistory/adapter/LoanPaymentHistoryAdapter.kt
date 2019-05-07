package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.loanHistory.model.ResPaymentHistory

class LoanPaymentHistoryAdapter (val context: Context, private val loanPaymentList: ArrayList<ResPaymentHistory.PaymentHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        viewHolder.txtTxnId.text=loanPaymentList[position].txn_id
        viewHolder.txtAmount.text=loanPaymentList[position].amount

       /* if(loanPaymentList[position].status=="Pending")
        {

        }*/


    }

    override fun getItemCount(): Int = loanPaymentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_payment_history_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtTxnId      : CommonTextRegular              = itemView.findViewById(R.id.txnId) as CommonTextRegular
            var txtAmount     : CommonTextRegular              = itemView.findViewById(R.id.txtAmount) as CommonTextRegular


        }


    }
}