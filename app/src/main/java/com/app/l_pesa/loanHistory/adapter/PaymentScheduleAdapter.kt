package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule


class PaymentScheduleAdapter(val context: Context, var alScheduleOBJ: ArrayList<ResPaybackSchedule.Schedule>, var loanInfo: ResPaybackSchedule.LoanInfo) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder

        if(alScheduleOBJ[position].paidStatus=="C")
        {
            viewHolder.llPayment.visibility     = View.GONE
            viewHolder.llPaid.visibility        = View.VISIBLE
            viewHolder.txtRepayAmount.text      = loanInfo.currencyCode+" "+alScheduleOBJ[position].paidAmount.toString()
            viewHolder.txtRepayDate.text        = alScheduleOBJ[position].sDate
            viewHolder.txtCurrentBalance.text   = "0"
            viewHolder.txtPaidDate.text         = fromHtml("<font color='#a4a4a4'>"+context.resources.getString(R.string.date)+"</font>"+"<font color='#333333'>"+" "+alScheduleOBJ[position].paidDate+"</font>")
        }
        else
        {
            viewHolder.llPaid.visibility        = View.GONE
            viewHolder.llPayment.visibility     = View.VISIBLE

            viewHolder.txtRepayAmount.text      = alScheduleOBJ[position].paidAmount.toString()
            viewHolder.txtRepayDate.text        = alScheduleOBJ[position].sDate
            viewHolder.txtCurrentBalance.text   = loanInfo.currencyCode+" "+loanInfo.currentBalance.toString()

            if(alScheduleOBJ[position].payanytime!!.btnStatus=="disable")
            {

                viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_button_red)
            }
            else
            {

                viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_button_green)
            }

            viewHolder.btnPayNow.setOnClickListener {

                if(alScheduleOBJ[position].payanytime!!.btnStatus=="disable")
                {

                   Toast.makeText(context,alScheduleOBJ[position].payanytime!!.alertMgs,Toast.LENGTH_SHORT).show()
                }
                else
                {

                    val alertDialog         = AlertDialog.Builder(context).create()
                    val inflater            = LayoutInflater.from(context)
                    val dialogView          = inflater.inflate(R.layout.dialog_payment_schedule, null)
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    alertDialog!!.setCancelable(true)
                    alertDialog.setCanceledOnTouchOutside(true)
                    alertDialog.setView(dialogView)
                    alertDialog.show()

                    val txtTitle   = dialogView.findViewById<CommonTextRegular>(R.id.txtTitle)
                    val txtContent = dialogView.findViewById<TextView>(R.id.txtContent)
                    val txtData    = dialogView.findViewById<TextView>(R.id.txtData)

                    txtTitle.text   =   loanInfo.payment_message!!.header
                    txtContent.text =   loanInfo.payment_message!!.header2
                    txtData.text    =   "Amount to pay is: "+loanInfo.currencyCode+" "+alScheduleOBJ[position].paidAmount.toString()+"\n"+
                                        "Reference number is: "+loanInfo.identityNumber+"\n"+
                                        "L-Pesa Short code is: "+loanInfo.merchantCode.toString()

                }
            }
        }


    }

    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    override fun getItemCount(): Int {

        return alScheduleOBJ.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.layout_payment_schedule, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


           var  txtRepayAmount:TextView        =itemView.findViewById(R.id.txt_repay_amount) as TextView
           var  txtRepayDate:TextView          =itemView.findViewById(R.id.txt_repay_date) as TextView
           var  txtCurrentBalance:TextView     =itemView.findViewById(R.id.txt_current_balance) as TextView
           var  txtPaidDate:TextView           =itemView.findViewById(R.id.txt_paid_date) as TextView
           var  llPayment:LinearLayout         =itemView.findViewById(R.id.llPayment) as LinearLayout
           var  llPaid:LinearLayout            =itemView.findViewById(R.id.llPaid) as LinearLayout
           var  btnPayNow:CustomButtonRegular  =itemView.findViewById(R.id.btnPayNow) as CustomButtonRegular

        }


    }

}