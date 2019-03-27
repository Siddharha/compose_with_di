package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.lpk.model.ResTransferHistory

class AdapterTransferHistory (val context: Context, private val listTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.txtToken.text = context.resources.getString(R.string.transfer_token)+":"+listTransferHistory[position].tokens
        viewHolder.txtRef.text = context.resources.getString(R.string.ref_no)+listTransferHistory[position].identityNumber

        if(listTransferHistory[position].status=="L")
        {
            viewHolder.buttonStatus.text = context.resources.getString(R.string.lock)
            viewHolder.buttonStatus.setBackgroundResource(R.drawable.blue_button)
        }
        else
        {
            viewHolder.buttonStatus.text = context.resources.getString(R.string.unlock)
            viewHolder.buttonStatus.setBackgroundResource(R.drawable.bg_button_green)
        }

    }

    override fun getItemCount(): Int = listTransferHistory.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_transfer_history, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtRef          : TextView = itemView.findViewById(R.id.txtRef) as CommonTextRegular
            var txtToken        : TextView = itemView.findViewById(R.id.txtToken) as CommonTextRegular
            var buttonStatus    : TextView = itemView.findViewById(R.id.buttonStatus) as CustomButtonRegular

        }

    }

}