package com.app.l_pesa.profile.adapter

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId

class PersonalIdListAdapter (val context: Context, private val listIdType: ArrayList<ResDashboard.PersonalIdType>, private val dialogOBJ: Dialog,private val callBack: ICallBackClickPersonalId) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.titleText.text = listIdType[position].name
        viewHolder.rlRootObj.setOnClickListener {
            dialogOBJ.dismiss()
            callBack.onSelectIdType(listIdType[position].id,listIdType[position].name,listIdType[position].type)
        }

    }

    override fun getItemCount(): Int = listIdType.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_title, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var titleText : TextView = itemView.findViewById(R.id.txt_country_name) as TextView
            var rlRootObj : RelativeLayout = itemView.findViewById(R.id.rlRoot) as RelativeLayout

        }

    }

}