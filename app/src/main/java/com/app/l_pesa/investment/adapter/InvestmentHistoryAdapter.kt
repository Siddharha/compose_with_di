package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.investment.inter.ICallBackEditHistory
import com.app.l_pesa.investment.model.ResInvestmentHistory
import kotlinx.android.synthetic.main.layout_investment_history_list.view.*

class InvestmentHistoryAdapter (val context: Context, private val investmentHistoryList: ArrayList<ResInvestmentHistory.UserInvestment>, private val callBack:ICallBackEditHistory) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_investment_history_list, parent, false))
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

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, investmentList: ResInvestmentHistory.UserInvestment, callBack: ICallBackEditHistory)
        {

            itemView.txtRate.text             = fromHtml(context.resources.getString(R.string.interest_rate)+"<font color='#3b3e42'>"+" "+ investmentList.deposit_interest_rate.toString()+"%"+"</font>")
            itemView.txtRef.text              = context.resources.getString(R.string.ref_no)+" "+investmentList.identity_number
            itemView.txtDuration.text         = fromHtml(context.resources.getString(R.string.months)+"<font color='#3b3e42'>"+" "+investmentList.deposit_month.toString()+"</font>")
            itemView.txtAppliedDate.text      = CommonMethod.dateConvert(investmentList.applied_date)

            if(investmentList.deposit_status!="P")
            {
               // itemView.txtMaturityDate.text     = CommonMethod.dateConvert(investmentList.maturity_date)
            }

            if(investmentList.deposit_status=="IA")
            {
                itemView.imageView5.visibility=View.INVISIBLE
                itemView.txtInterest.visibility=View.INVISIBLE
                itemView.txtInterest.visibility=View.INVISIBLE
                itemView.imageView6.visibility=View.INVISIBLE
               // itemView.textView8.visibility=View.INVISIBLE
               // itemView.txtMaturityDate.visibility=View.INVISIBLE
                /*itemView.txtStatus.visibility=View.VISIBLE
                itemView.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                itemView.txtStatus.text=investmentList.deposit_status_txt
                itemView.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_red,0,0,0)*/
            }
            else if(investmentList.deposit_status=="P")
            {
                itemView.imageView5.visibility=View.INVISIBLE
                itemView.txtInterest.visibility=View.INVISIBLE
                itemView.txtInterest.visibility=View.INVISIBLE
                itemView.imageView6.visibility=View.INVISIBLE
                //itemView.textView8.visibility=View.INVISIBLE
               // itemView.txtMaturityDate.visibility=View.INVISIBLE
               /* itemView.txtStatus.visibility=View.VISIBLE
                itemView.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
                itemView.txtStatus.text=investmentList.deposit_status_txt
                itemView.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_yellow,0,0,0)*/
            }
            else if(investmentList.deposit_status=="C")
            {

                itemView.imageView5.visibility=View.VISIBLE
                itemView.txtInterest.visibility=View.VISIBLE
                itemView.txtInterest.visibility=View.VISIBLE
                itemView.imageView6.visibility=View.VISIBLE
               // itemView.textView8.visibility=View.VISIBLE
               // itemView.txtMaturityDate.visibility=View.VISIBLE


               // itemView.txtAmount.text=investmentList.currency_code+" "+investmentList.maturity_amount.toString()
            }

            else if(investmentList.deposit_status=="A")
            {

                itemView.txtDetails.visibility=View.GONE
                itemView.imageView5.visibility=View.VISIBLE
                itemView.txtInterest.visibility=View.VISIBLE
                itemView.txtMaturity.visibility=View.VISIBLE
                itemView.imageView6.visibility=View.VISIBLE
                itemView.imageView7.setImageResource(R.drawable.ic_approved_icon)

                itemView.txtAmount.text=fromHtml("<font color='#3b3e42'>"+ investmentList.currency_code+" "+investmentList.deposit_amount.toString()+"</font>")
                itemView.txtMaturity.text=fromHtml(context.resources.getString(R.string.maturity_on)+": "+"<font color='#3b3e42'>"+ CommonMethod.dateConvert(investmentList.maturity_date)+"</font>")
                itemView.txtInterest.text=fromHtml(context.resources.getString(R.string.interest)+": "+"<font color='#3b3e42'>"+ investmentList.currency_code+" "+investmentList.interest_amount.toString()+"</font>")
                itemView.txtWithdrawalStatus.text=context.getString(R.string.active)
                itemView.txtWithdrawalStatus.setTextColor(ContextCompat.getColor(context,R.color.color_deep_green))

            }

            if(!investmentList.actionState.btnWithdrawalShow && !investmentList.actionState.btnReinvestShow && !investmentList.actionState.btnExitPointShow)
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

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}