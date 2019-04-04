package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.investment.inter.ICallBackEditHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory

class InvestmentHistoryAdapter (val context: Context, private val investmentList: ArrayList<ResInvestmentHistory.UserInvestment>, private val callBack:ICallBackEditHistory) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        viewHolder.txtInterestRate.text     = context.resources.getString(R.string.rate)+": "+investmentList[position].depositInterestRate.toString()+"%"
        viewHolder.txtRef.text              = context.resources.getString(R.string.ref_no)+" "+investmentList[position].identityNumber
        if(!TextUtils.isEmpty(investmentList[position].maturityAmount))
        {
            viewHolder.txtAmount.text       = investmentList[position].currencyCode+" "+investmentList[position].maturityAmount
        }

        viewHolder.txtDepositAmount.text    = investmentList[position].currencyCode+" "+investmentList[position].depositAmount.toString()
        viewHolder.txtMaturityDate.text     = investmentList[position].maturityDate
        viewHolder.txtDuration.text         = context.resources.getString(R.string.months)+" "+investmentList[position].depositMonth.toString()
        viewHolder.txtAppliedDate.text      = CommonMethod.dateConvert(investmentList[position].applied_date)

        if(investmentList[position].depositStatus=="IA")
        {

            viewHolder.imageView5.visibility=View.INVISIBLE
            viewHolder.textView6.visibility=View.INVISIBLE
            viewHolder.txtAmount.visibility=View.INVISIBLE
            viewHolder.imageView6.visibility=View.INVISIBLE
            viewHolder.textView8.visibility=View.INVISIBLE
            viewHolder.txtMaturityDate.visibility=View.INVISIBLE
        }
        else if(investmentList[position].depositStatus=="A")
        {

        }
        if(investmentList[position].depositStatus=="C")
        {

        }

        viewHolder.imgEdit.setOnClickListener {

            callBack.onEditWindow(viewHolder.imgEdit)


        }

    }

    override fun getItemCount(): Int = investmentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_investment_history_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtRef              : TextView = itemView.findViewById(R.id.txtRef) as TextView
            var txtAmount           : TextView = itemView.findViewById(R.id.txtAmount) as TextView
            var txtMaturityDate     : TextView = itemView.findViewById(R.id.txtMaturityDate) as TextView
            var txtInterestRate     : TextView = itemView.findViewById(R.id.txtInterestRate) as TextView
            var txtDepositAmount    : TextView = itemView.findViewById(R.id.txtDepositAmount) as TextView
            var txtDuration         : TextView = itemView.findViewById(R.id.txtDuration) as TextView
            var txtAppliedDate      : TextView = itemView.findViewById(R.id.txtAppliedDate) as TextView
            var imgEdit             : ImageButton = itemView.findViewById(R.id.imgEdit) as ImageButton
            var imageView5          : ImageView   = itemView.findViewById(R.id.imageView5) as ImageView
            var textView6           : TextView   = itemView.findViewById(R.id.textView6) as TextView
            var textView8           : TextView   = itemView.findViewById(R.id.textView8) as TextView
            var imageView6          : ImageView   = itemView.findViewById(R.id.imageView6) as ImageView


        }


    }
}