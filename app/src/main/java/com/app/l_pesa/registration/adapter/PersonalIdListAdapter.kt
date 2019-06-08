package com.app.l_pesa.registration.adapter

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.profile.inter.ICallBackId

class PersonalIdListAdapter (val context: Context, private val idText: ArrayList<String>,private val nameText: ArrayList<String>, private val dialogOBJ: Dialog, private val callBack: ICallBackId) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.titleText.text = nameText[position]
        viewHolder.rlRootObj.setOnClickListener {
            dialogOBJ.dismiss()
            callBack.onClickIdType(position,nameText[position])
        }

    }

    override fun getItemCount(): Int = idText.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val recyclerView: androidx.recyclerview.widget.RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_title, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

            var titleText : TextView = itemView.findViewById(R.id.txt_country_name) as TextView
            var rlRootObj : RelativeLayout = itemView.findViewById(R.id.rlRoot) as RelativeLayout

        }


    }

}