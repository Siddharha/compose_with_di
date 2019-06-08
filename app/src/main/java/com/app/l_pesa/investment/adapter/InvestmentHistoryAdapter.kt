package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.investment.inter.ICallBackEditHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import kotlinx.android.synthetic.main.layout_investment_history_list.view.*
import java.text.DecimalFormat

class InvestmentHistoryAdapter (val context: Context, private val investmentHistoryList: ArrayList<ResInvestmentHistory.UserInvestment>, private val callBack:ICallBackEditHistory) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_investment_history_list, parent, false))
        } else {
            LoadingViewHolder(inflater.inflate(R.layout.layout_load_more, parent, false))
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading)
        {
            isLoading = true
            loadMoreListener.onLoadMore()
        }

        if (getItemViewType(position) == 0) {
            (holder as UserViewHolder).bindData(context, investmentHistoryList[position],callBack)


        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(investmentHistoryList[position].investment_id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return investmentHistoryList.size
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

    class UserViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {



        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, investmentList: ResInvestmentHistory.UserInvestment, callBack: ICallBackEditHistory)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            itemView.txtRate.text             = fromHtml(context.resources.getString(R.string.rate)+"<font color='#777b7e'>"+": "+ format.format(investmentList.deposit_interest_rate).toString()+"%"+"</font>")
            itemView.txtRef.text              = fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.ref_no)+"</font>"+" "+investmentList.identity_number)
            itemView.txtDuration.text         = investmentList.deposit_month.toString()+" Months"
            itemView.txtAppliedDate.text      = CommonMethod.dateConvert(investmentList.applied_date)
            itemView.txtAmount.text=fromHtml("<font color='#777b7e'>"+ investmentList.currency_code+" "+format.format(investmentList.deposit_amount).toString()+"</font>")


            if(investmentList.deposit_status=="IA")
            {
                itemView.imageView5.visibility=View.INVISIBLE
                itemView.txtInterest.visibility=View.INVISIBLE
                itemView.imageView6.visibility=View.VISIBLE
                itemView.imageView7.visibility=View.INVISIBLE
                itemView.txtWithdrawalStatus.visibility=View.INVISIBLE
                itemView.txtMaturity.visibility=View.VISIBLE
                itemView.txtDetails.visibility=View.GONE

                itemView.txtMaturity.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                itemView.txtMaturity.text=investmentList.deposit_status_txt
                itemView.imageView6.setImageResource(R.drawable.ic_error_red)

            }
            else if(investmentList.deposit_status=="P")
            {
                itemView.imageView5.visibility=View.INVISIBLE
                itemView.txtInterest.visibility=View.INVISIBLE
                itemView.imageView7.visibility=View.INVISIBLE
                itemView.txtWithdrawalStatus.visibility=View.INVISIBLE
                itemView.txtMaturity.visibility=View.VISIBLE
                itemView.imageView6.visibility=View.VISIBLE
                itemView.txtDetails.visibility=View.GONE

                itemView.txtMaturity.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
                itemView.txtMaturity.text=investmentList.deposit_status_txt
                itemView.imageView6.setImageResource(R.drawable.ic_error_yellow)

            }
            else if(investmentList.deposit_status=="C")
            {

                itemView.imageView5.visibility      =View.VISIBLE
                itemView.txtInterest.visibility     =View.VISIBLE
                itemView.imageView6.visibility      =View.VISIBLE
                itemView.txtDetails.visibility      =View.VISIBLE
                itemView.txtMaturity.visibility     =View.VISIBLE
                itemView.txtWithdrawalStatus.visibility=View.INVISIBLE //Visible
                itemView.imageView7.visibility=View.INVISIBLE //Visible
                itemView.imageView7.setImageResource(R.drawable.ic_calendar_icon)
                itemView.imageView6.setImageResource(R.drawable.ic_calendar_icon)
                itemView.txtMaturity.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemView.txtMaturity.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.maturity)+": "+"</font>"+"<font color='#777b7e'>"+ CommonMethod.dateConvert(investmentList.maturity_date)+"</font>")
                itemView.txtInterest.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.interest)+": "+"</font>"+"<font color='#777b7e'>"+ investmentList.currency_code+" "+format.format(investmentList.interest_amount).toString()+"</font>")
                itemView.txtDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_grey,0,0,0)
                itemView.txtDetails.text = investmentList.belowMessage
                itemView.txtWithdrawalStatus.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.withdraw_on)+": "+"</font>"+"<font color='#777b7e'>"+ CommonMethod.dateConvert(investmentList.withdraw_date)+"</font>")


            }

            else if(investmentList.deposit_status=="A")
            {

                itemView.txtDetails.visibility=View.GONE
                itemView.imageView5.visibility=View.VISIBLE
                itemView.txtInterest.visibility=View.VISIBLE
                itemView.txtMaturity.visibility=View.VISIBLE
                itemView.imageView6.visibility=View.VISIBLE
                itemView.imageView7.visibility=View.VISIBLE
                itemView.txtWithdrawalStatus.visibility=View.VISIBLE
                itemView.imageView6.setImageResource(R.drawable.ic_calendar_icon)
                itemView.imageView7.setImageResource(R.drawable.ic_approved_icon)
                itemView.txtMaturity.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemView.txtMaturity.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.maturity)+": "+"</font>"+"<font color='#777b7e'>"+ CommonMethod.dateConvert(investmentList.maturity_date)+"</font>")
                itemView.txtInterest.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.interest)+": "+"</font>"+"<font color='#777b7e'>"+ investmentList.currency_code+" "+format.format(investmentList.interest_amount).toString()+"</font>")
                itemView.txtWithdrawalStatus.text=context.getString(R.string.active)
                itemView.txtWithdrawalStatus.setTextColor(ContextCompat.getColor(context,R.color.color_deep_green))

            }
            else if(investmentList.deposit_status=="M")
            {
                itemView.txtDetails.visibility=View.GONE
                itemView.imageView5.visibility=View.VISIBLE
                itemView.txtInterest.visibility=View.VISIBLE
                itemView.txtMaturity.visibility=View.VISIBLE
                itemView.imageView7.visibility=View.INVISIBLE
                itemView.txtWithdrawalStatus.visibility=View.INVISIBLE
                itemView.txtMaturity.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemView.txtMaturity.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.maturity)+": "+"</font>"+"<font color='#777b7e'>"+ CommonMethod.dateConvert(investmentList.maturity_date)+"</font>")
                itemView.txtInterest.text=fromHtml("<font color='#777b7e'>"+context.resources.getString(R.string.interest)+": "+"</font>"+"<font color='#777b7e'>"+ investmentList.currency_code+" "+format.format(investmentList.interest_amount).toString()+"</font>")

            }

            if(!investmentList.actionState.btnWithdrawalShow && !investmentList.actionState.btnReinvestShow && !investmentList.actionState.btnExitPointShow && investmentList.deposit_status!="P")
            {
                itemView.imgEdit.visibility=View.INVISIBLE
            }
            else
            {
                itemView.imgEdit.visibility=View.VISIBLE
                itemView.imgEdit.setOnClickListener {

                    callBack.onEditWindow(itemView.imgEdit,investmentList)
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

    class LoadingViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}