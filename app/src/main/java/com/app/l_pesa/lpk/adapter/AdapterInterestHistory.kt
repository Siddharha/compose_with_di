package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.lpk.model.ResInterestHistory

class AdapterInterestHistory (val context: Context, private val listTransferHistory: ArrayList<ResInterestHistory.UserInterestHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder


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

           // var txtRef          : TextView = itemView.findViewById(R.id.txtRef) as CommonTextRegular


        }

    }

}