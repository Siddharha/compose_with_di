package com.app.l_pesa.loanplan.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId
import com.app.l_pesa.profile.model.ResUserInfo
import java.util.*

class PersonalIdAdapter (val context: Context,private val userIdsPersonalInfo: ArrayList<ResUserInfo.UserIdsPersonalInfo>, private val callBack: ICallBackClickPersonalId) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        viewHolder.txtIdType.text       = context.resources.getString(R.string.id_type)+" "+userIdsPersonalInfo[position].idTypeName
        viewHolder.txtUploadTime.text   = context.resources.getString(R.string.uploaded_on)+" "+userIdsPersonalInfo[position].created

        if(userIdsPersonalInfo[position].verified==1)
        {
            viewHolder.imgVerifiedStatus.visibility=View.VISIBLE
        }
        else
        {
            viewHolder.imgVerifiedStatus.visibility=View.INVISIBLE
        }

        if(userIdsPersonalInfo[position].typeName=="Personal" && userIdsPersonalInfo[position].idTypeUnique=="Identity Proof")
        {

            viewHolder.txtIdNumber.visibility=View.VISIBLE
            viewHolder.txtIdNumber.text=context.resources.getString(R.string.id_number)+" "+userIdsPersonalInfo[position].idNumber
        }
        else
        {
            viewHolder.txtIdNumber.visibility=View.GONE
        }

        viewHolder.imgEdit.setOnClickListener {


            callBack.onClickIdList(userIdsPersonalInfo[position],position,it)
        }


    }

    override fun getItemCount(): Int = userIdsPersonalInfo.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_id_information, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtIdNumber          : TextView     = itemView.findViewById(R.id.txtIdNumber) as TextView
            var txtIdType            : TextView     = itemView.findViewById(R.id.txtIdType) as TextView
            var txtUploadTime        : TextView     = itemView.findViewById(R.id.txtUploadTime) as TextView
            var imgVerifiedStatus    : ImageView    = itemView.findViewById(R.id.imgVerifiedStatus) as ImageView
            var imgEdit              : ImageButton  = itemView.findViewById(R.id.imgEdit) as ImageButton

        }


    }
}