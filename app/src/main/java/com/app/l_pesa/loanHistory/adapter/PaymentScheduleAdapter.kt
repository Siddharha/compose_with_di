package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import kotlinx.android.synthetic.main.layout_payment_schedule.view.*


class PaymentScheduleAdapter(val context: Context, var alScheduleOBJ: ArrayList<ResPaybackSchedule.Schedule>, var loanInfo: ResPaybackSchedule.LoanInfo) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_payment_schedule, parent, false))
        } else {
            LoadingViewHolder(inflater.inflate(R.layout.layout_load_more, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading)
        {
            isLoading = true
            loadMoreListener.onLoadMore()
        }

        if (getItemViewType(position) == 0) {
            (holder as UserViewHolder).bindData(context, alScheduleOBJ[position],loanInfo)


        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(alScheduleOBJ[position].loan_history_id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return alScheduleOBJ.size
    }


    fun notifyDataChanged() {
        notifyDataSetChanged()
        isLoading = false
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    internal fun setLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        this.loadMoreListener = loadMoreListener
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, alScheduleOBJ: ResPaybackSchedule.Schedule, loanInfo: ResPaybackSchedule.LoanInfo)
        {

           
            if(alScheduleOBJ.paid_status=="C")
            {
                itemView.llPayment.visibility       = View.GONE
                itemView.llPaid.visibility          = View.VISIBLE
                itemView.txt_repay_amount.text      = alScheduleOBJ.currency_code+" "+alScheduleOBJ.paid_amount.toString()
                itemView.txt_repay_date.text        = CommonMethod.dateConvert((alScheduleOBJ.sDate))
                itemView.txt_current_balance.text   = alScheduleOBJ.currency_code+" 0"
                itemView.txt_paid_date.text         = fromHtml("<font color='#61666b'>"+" "+CommonMethod.dateTimeConvert((alScheduleOBJ.paid_date))+"</font>")
            }
            else
            {
                itemView.llPaid.visibility          = View.GONE
                itemView.llPayment.visibility       = View.VISIBLE
                itemView.txt_repay_amount.text      = alScheduleOBJ.currency_code+" "+alScheduleOBJ.paid_amount.toString()
                itemView.txt_current_balance.text   = alScheduleOBJ.currency_code+" "+loanInfo.currentBalance.toString()
                itemView.txt_paid_date.text         = CommonMethod.dateConvert((alScheduleOBJ.sDate))
                itemView.btnPayNow.text             = alScheduleOBJ.payanytime.btnText
                itemView.btnPayNow.setTextColor(Color.parseColor(alScheduleOBJ.payanytime.btnHexColor))

                if(alScheduleOBJ.paid_status=="P")
                {

                    if(alScheduleOBJ.payanytime.btnStatus=="disable")
                    {

                        itemView.btnPayNow.setBackgroundResource(R.drawable.bg_transparent_border_orange_view)

                    }
                    else
                    {
                        if(alScheduleOBJ.payanytime.btnColor.contentEquals("green"))
                        {
                            itemView.btnPayNow.setBackgroundResource(R.drawable.bg_transparent_border_green_view)
                        }
                        else
                        {
                            itemView.btnPayNow.setBackgroundResource(R.drawable.bg_transparent_border_red_view)
                        }
                    }

                }
                else
                {

                    itemView.btnPayNow.setBackgroundResource(R.drawable.bg_button_green)
                }

                itemView.btnPayNow.setOnClickListener {

                    if(alScheduleOBJ.payanytime.btnStatus=="disable")
                    {

                        Toast.makeText(context,alScheduleOBJ.payanytime.alertMgs,Toast.LENGTH_SHORT).show()
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
                        txtData.text    =   "Amount to pay is: "+loanInfo.currencyCode+" "+alScheduleOBJ.payanytime.repayAmount.toString()+"\n"+
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

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)



}