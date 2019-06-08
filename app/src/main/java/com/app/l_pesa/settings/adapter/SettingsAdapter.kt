package com.app.l_pesa.settings.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.settings.inter.ICallBackListClick
import com.app.l_pesa.settings.model.SettingsItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_settings_item.view.*


/**
 * Created by Intellij Amiya on 04-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

class SettingsAdapter(private val context: Context, private val items: List<SettingsItem>, private val callBack: ICallBackListClick)

    : androidx.recyclerview.widget.RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position],position,callBack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_settings_item, parent, false))

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bindItem(items: SettingsItem,position: Int, callBack: ICallBackListClick){
            itemView.txtSetting.text = items.name
            Glide.with(itemView.context)
                    .load(items.image).
                    into(itemView.imgSetting)

            itemView.llRoot.setOnClickListener {

                callBack.onClickListItem(position)
            }
        }
    }

}