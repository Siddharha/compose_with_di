package com.app.l_pesa.profile.adapter

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.profile.inter.ICallBackTitle

class TitleListAdapter (val context: Context, private val titleText: ArrayList<String>,private val titleIcon: ArrayList<Int>, private val dialogOBJ: Dialog, private val callBack:ICallBackTitle) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.titleText.text = titleText[position]
        viewHolder.titleIcon.setImageResource(titleIcon[position])
        viewHolder.rlRootObj.setOnClickListener {
            dialogOBJ.dismiss()
            callBack.onChangeTitle(titleText[position])
        }

    }

    override fun getItemCount(): Int = titleText.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val recyclerView: androidx.recyclerview.widget.RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_text_icon, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

            var titleText : TextView = itemView.findViewById(R.id.txt_country_name) as TextView
            var rlRootObj : RelativeLayout = itemView.findViewById(R.id.rlRoot) as RelativeLayout
            var titleIcon : ImageView =itemView.findViewById(R.id.img_flag)


        }


    }

}