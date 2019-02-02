package com.app.l_pesa.common

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.app.l_pesa.R

class NavigationDrawerAdapter(private var context: Context, private var data: List<NavDrawerItem>) : RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder>() {

    var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.nav_drawer_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current         = data[position]
        holder.txtMenu.text   = current.title
         setImageIcon(position, holder)
    }

    override fun getItemCount(): Int = data.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtMenu: TextView = itemView.findViewById(R.id.txtMenu)
        var imgMenu: ImageView = itemView.findViewById(R.id.imgMenu)

    }

    init {
        inflater = LayoutInflater.from(context)
    }

    private fun setImageIcon(position: Int, holder: MyViewHolder) {
        when (position) {
            /*0 -> holder.img_icon.setImageResource(R.drawable.ic_dashboard)
            1 -> holder.img_icon.setImageResource(R.drawable.ic_view_loan_plan)
            2 -> holder.img_icon.setImageResource(R.drawable.ic_my_profile)
            3 -> holder.img_icon.setImageResource(R.drawable.ic_loan_history)
            4 -> holder.img_icon.setImageResource(R.drawable.ic_buy_point_icon)
            5 -> holder.img_icon.setImageResource(R.drawable.ic_refer_frind_icon)
            6 -> holder.img_icon.setImageResource(R.drawable.ic_help_icon)
            7 -> holder.img_icon.setImageResource(R.drawable.ic_settings_icon)
            else -> holder.img_icon.setImageResource(R.drawable.ic_logout_icon)*/
        }
    }
}
