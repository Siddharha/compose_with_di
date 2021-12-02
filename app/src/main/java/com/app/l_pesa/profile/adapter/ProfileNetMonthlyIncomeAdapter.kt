package com.app.l_pesa.profile.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.profile.inter.ICallBackAdditionalInfoDropdown
import com.app.l_pesa.profile.inter.ICallBackClickMoreAbout
import com.app.l_pesa.profile.inter.ICallBackProfileAdditionalInfo
import com.app.l_pesa.profile.model.ResUserAdditionalInfoDropdowns

class ProfileNetMonthlyIncomeAdapter (val context: Context, private val listIdType: ArrayList<ResUserAdditionalInfoDropdowns.Data.NetMonthlyIncome>, private val dialogOBJ: Dialog, private val callBack: ICallBackAdditionalInfoDropdown) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        viewHolder.titleText.text = listIdType[position].name
        viewHolder.rlRootObj.setOnClickListener {
            dialogOBJ.dismiss()
            callBack.onDropdownNetMonthlyIncomeSelected(listIdType[position])
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