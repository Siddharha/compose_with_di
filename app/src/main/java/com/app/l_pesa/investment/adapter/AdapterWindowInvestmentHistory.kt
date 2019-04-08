package com.app.l_pesa.investment.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.investment.model.ModelWindowHistory
import com.app.l_pesa.profile.inter.ICallBackRecyclerCallbacks

class AdapterWindowInvestmentHistory(val context: Context) : RecyclerView.Adapter<AdapterWindowInvestmentHistory.MyViewHolder>() {

    var filerList : List<ModelWindowHistory> = mutableListOf()
    var callback: ICallBackRecyclerCallbacks<ModelWindowHistory>? = null
    private var selectedItem: Int = -1

    fun addAlertFilter(filers: List<ModelWindowHistory>) {
        filerList = filers.toMutableList()
        notifyDataSetChanged()
    }

    fun selectedItem(position: Int){
        selectedItem = position
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, p1: Int) {
        val item = filerList[p1]
        if(!filerList[p1].status)
        {
            holder.tvName.isEnabled=false
        }
        holder.tvName.text = item.name

    }

    fun setOnClick(click: ICallBackRecyclerCallbacks<ModelWindowHistory>){
        callback = click
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.layout_popup_window_investment,p0,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filerList.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        var tvName      : TextView          = itemView.findViewById(R.id.txtData)
        var rootLayout  : ConstraintLayout  = itemView.findViewById(R.id.rootLayout)

        init {
            setClickListener(rootLayout)
        }

        private fun setClickListener(view: View) {
            view.setOnClickListener {
                callback?.onItemClick(it, adapterPosition, filerList[adapterPosition])
            }
        }

    }

}