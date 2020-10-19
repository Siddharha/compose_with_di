package com.app.l_pesa.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.profile.model.statement.StatementListResponse
import com.app.l_pesa.profile.view.ProfileEditStatementInfoActivity
import kotlinx.android.synthetic.main.statement_list_cell.view.*


class StatementListAdapter (
        private val arrayList: List<StatementListResponse.Data>,
        private val context: Context,
        private val layout: Int) : RecyclerView.Adapter<StatementListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: StatementListResponse.Data) {

            with(itemView){
                tvName.text = item.typeName
                tvFile.text = item.fileName
                tvLastCreated.text = item.created

                if (item.verified ==1){
                    imgVerified.visibility = View.VISIBLE
                }else{
                    imgVerified.visibility = View.GONE
                }

                imgMore.setOnClickListener {
                    (context as ProfileEditStatementInfoActivity).listPopup(context,it,item)
                }
                }

            }


        }
    }