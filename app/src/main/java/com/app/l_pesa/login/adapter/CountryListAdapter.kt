package com.app.l_pesa.login.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.splash.model.ResModelCountryList


class CountryListAdapter(val context: Context,private var countryList: ArrayList<ResModelCountryList>, private val dialogOBJ: Dialog,private val callBackOBJ:ICallBackCountryList) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder
        if(countryList[position].id==0)
        {
            viewHolder.txtCountry.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
        }
        else
        {
            viewHolder.txtCountry.setTextColor(ContextCompat.getColor(context,R.color.textColors))
        }

        viewHolder.txtCountry.text = countryList[position].country_name
        viewHolder.txtCountry.isAllCaps=true
        viewHolder.rlRootObj.setOnClickListener {

            if(!countryList[position].country_name.contentEquals(context.getString(R.string.search_result_not_found)))
            {
                dialogOBJ.dismiss()
                callBackOBJ.onClickCountry(countryList[position])
            }

        }


    }

    override fun getItemCount(): Int = countryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_country, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtCountry: TextView        = itemView.findViewById(R.id.txt_country_name) as TextView
            var rlRootObj : RelativeLayout  = itemView.findViewById(R.id.rlRoot) as RelativeLayout

        }


    }

    // To get the data to search Category
    fun filterList(filteredCourseList: ArrayList<ResModelCountryList>) {
        this.countryList = filteredCourseList
        notifyDataSetChanged()
    }

}