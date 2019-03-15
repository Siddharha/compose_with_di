package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.app.l_pesa.R
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
            viewHolder.txtRepayAmount.text      = alScheduleOBJ[position].paidAmount.toString()
            viewHolder.txtRepayDate.text        = alScheduleOBJ[position].sDate
            viewHolder.txtCurrentBalance.text   = "0"
            viewHolder.txtPaidDate.text         = context.resources.getString(R.string.paid_on)+" "+alScheduleOBJ[position].paidDate
        }
        else
        {
            viewHolder.llPaid.visibility        = View.GONE
            viewHolder.llPayment.visibility     = View.VISIBLE

            viewHolder.txtRepayAmount.text      = alScheduleOBJ[position].paidAmount.toString()
            viewHolder.txtRepayDate.text        = alScheduleOBJ[position].sDate
            viewHolder.txtCurrentBalance.text   = loanInfo.currentBalance.toString()

            if(alScheduleOBJ[position].payanytime!!.btnStatus=="disable")
            {

                viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_button_red)
            }
            else
            {

                viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_button_green)
            }
        }

        /*if(!al_loadOBJ[position].paidAmount.isEmpty())
        {
           viewHolder.txtRepayAmount.text=al_loadOBJ[position].paidAmount
        }
        if(!al_loadOBJ[position].sDate.isEmpty())
        {
            viewHolder.txtRepayDate.text=al_loadOBJ[position].sDate
        }


        if(!al_loadOBJ[position].paidStatus.isEmpty())
        {

            if (al_loadOBJ[position].paidStatus =="C")
            {
                viewHolder.llPayment.visibility        = View.GONE
                viewHolder.llPaid.visibility           = View.VISIBLE
                viewHolder.txtPaidDate.text            = context.resources.getString(R.string.paid_on)+" "+al_loadOBJ[position].paidDate

            }

            else
            {
                *//*if (al_loadOBJ[position].canPay)
                {
                    viewHolder.llPaid.visibility            =   View.GONE
                    viewHolder.llPaid.visibility            =   View.VISIBLE
                    viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_button_green)
                }
                else
                {
                    viewHolder.llPaid.visibility                =   View.GONE
                    viewHolder.llPaid.visibility                =   View.VISIBLE
                    viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_button_grey)
                }*//*
            }


        }*/

        // On Click Functionality
       // viewHolder.rl_pay_now.setOnClickListener{

            /*if (al_loadOBJ[position].canPay)
            {

                val metrics = _contextOBJ!!.resources.displayMetrics
                val width = metrics.widthPixels
                val dialog = Dialog(_contextOBJ)
                dialog.setContentView(R.layout.dialog_dummy)
                dialog.window.setLayout(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                dialog.setCancelable(true)

                var txt_amount: TextView = dialog.findViewById<TextView>(R.id.txt_amount) as TextView
                var txt_ref: TextView = dialog.findViewById<TextView>(R.id.txt_ref) as TextView
                var txt_code: TextView = dialog.findViewById<TextView>(R.id.txt_code) as TextView

                txt_amount.text="Amount to pay is: " +al_loadOBJ[position].paid_amount
                txt_ref.text="Reference number is: "  +ref_no
                txt_code.text="L-Pesa Short code is: " +merchant_code

                var buttonOK: Button = dialog.findViewById<Button>(R.id.buttonOK) as Button
                buttonOK.setOnClickListener(View.OnClickListener {
                    dialog.dismiss()

                })

                dialog.show()


            }*/

        //}


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