package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import java.text.DecimalFormat


class PaymentScheduleAdapter(val context: Context, private var alScheduleOBJ: ArrayList<ResPaybackSchedule.Schedule>, private var loanInfo: ResPaybackSchedule.LoanInfo) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as SelectViewHolder

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        if(alScheduleOBJ[position].paidStatus=="C")
        {
            viewHolder.llPayment.visibility     = View.GONE
            viewHolder.llPaid.visibility        = View.VISIBLE
            viewHolder.txtRepayAmount.text      = loanInfo.currencyCode+" "+format.format(alScheduleOBJ[position].paidAmount).toString()
            viewHolder.txtRepayDate.text        = CommonMethod.dateConvert((alScheduleOBJ[position].sDate))
            viewHolder.txtCurrentBalance.text   = loanInfo.currencyCode+" 0"
            viewHolder.txtPaidDate.text         = fromHtml("<font color='#61666b'>"+" "+CommonMethod.dateConvert((alScheduleOBJ[position].paidDate))+"</font>")
        }
        else
        {
            viewHolder.llPaid.visibility        = View.GONE
            viewHolder.llPayment.visibility     = View.VISIBLE
            viewHolder.txtRepayAmount.text      = loanInfo.currencyCode+" "+format.format(alScheduleOBJ[position].paidAmount).toString()
            viewHolder.txtCurrentBalance.text   = loanInfo.currencyCode+" "+format.format(loanInfo.currentBalance).toString()
            viewHolder.txtRepayDate.text        = CommonMethod.dateConvert((alScheduleOBJ[position].sDate))
            viewHolder.btnPayNow.text           = alScheduleOBJ[position].payanytime!!.btnText
            viewHolder.btnPayNow.setTextColor(Color.parseColor(alScheduleOBJ[position].payanytime!!.btnHexColor))

            if(alScheduleOBJ[position].paidStatus=="P")
            {

                if(alScheduleOBJ[position].payanytime!!.btnStatus=="disable")
                {

                  viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_transparent_border_orange_view)

                }
                else
                {
                    if(alScheduleOBJ[position].payanytime!!.btnColor.contentEquals("green"))
                    {
                        viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_transparent_border_green_view)
                    }
                    else
                    {
                        viewHolder.btnPayNow.setBackgroundResource(R.drawable.bg_transparent_border_red_view)
                    }
                }

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
                    txtData.text    =   "Amount to pay is: "+loanInfo.currencyCode+" "+alScheduleOBJ[position].payanytime!!.repayAmount.toString()+"\n"+
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

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val recyclerView: androidx.recyclerview.widget.RecyclerView.ViewHolder
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.layout_payment_schedule, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


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