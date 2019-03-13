package com.app.l_pesa.profile.adapter

import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.profile.inter.ICallBackRecyclerviewCallbacks
import com.app.l_pesa.profile.model.ModelWindowPopUp

class AdapterPopupWindow(val context: Context) : RecyclerView.Adapter<AdapterPopupWindow.MyViewHolder>() {

    var filerList : List<ModelWindowPopUp> = mutableListOf()
    var callback: ICallBackRecyclerviewCallbacks<ModelWindowPopUp>? = null
    private var selectedItem: Int = -1

    fun addAlertFilter(filers: List<ModelWindowPopUp>) {
        filerList = filers.toMutableList()
        notifyDataSetChanged()
    }

    fun selectedItem(position: Int){
        selectedItem = position
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, p1: Int) {
        val item = filerList[p1]
        holder.tvName.text = item.name
        holder.imgIcon.background = ContextCompat.getDrawable(context, item.icon)

    }

    fun setOnClick(click: ICallBackRecyclerviewCallbacks<ModelWindowPopUp>){
        callback = click
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.action_item_vertical,p0,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filerList.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        var tvName      :TextView   = itemView.findViewById(R.id.alert_filter_name)
        var imgIcon     : ImageView = itemView.findViewById(R.id.alert_filter_icon)
        var imgSelected : ImageView = itemView.findViewById(R.id.alert_filter_selected)

        var filterLayout:ConstraintLayout = itemView.findViewById(R.id.alert_filter_item_layout)

        init {
            setClickListener(filterLayout)
        }

        private fun setClickListener(view: View) {
            view.setOnClickListener {
                callback?.onItemClick(it, adapterPosition, filerList[adapterPosition])
            }
        }

    }

}