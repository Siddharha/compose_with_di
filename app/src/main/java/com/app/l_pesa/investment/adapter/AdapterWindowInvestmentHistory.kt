package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.investment.inter.ICallBackPopUpWindow
import com.app.l_pesa.investment.model.ModelWindowHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import java.util.ArrayList

class AdapterWindowInvestmentHistory (val context: Context, private val listInvestmentHistory: ArrayList<ModelWindowHistory>, val investmentList: ResInvestmentHistory.UserInvestment, private val callBack:ICallBackPopUpWindow) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        viewHolder.tvName.text = listInvestmentHistory[position].name
        viewHolder.rootLayout.setOnClickListener {

            callBack.onItemClick(it,position,listInvestmentHistory[position].name,investmentList)
        }

    }

    override fun getItemCount(): Int = listInvestmentHistory.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_popup_window_investment, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var tvName      : TextView          = itemView.findViewById(R.id.txtData)
            var rootLayout  : ConstraintLayout  = itemView.findViewById(R.id.rootLayout)

        }


    }
}