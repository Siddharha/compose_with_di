package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
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

            itemView.txtRate.text             = investmentList.deposit_interest_rate.toString()+"%"
            itemView.txtRef.text              = context.resources.getString(R.string.ref_no)+" "+investmentList.identity_number
            itemView.txtDepositAmount.text    = investmentList.currency_code+" "+investmentList.deposit_amount.toString()

            itemView.txtDurationTime.text     = investmentList.deposit_month.toString()
            itemView.txtAppliedDate.text      = CommonMethod.dateConvert(investmentList.applied_date)

            if(investmentList.deposit_status!="P")
            {
                itemView.txtMaturityDate.text     = CommonMethod.dateConvert(investmentList.maturity_date)
            }

            if(investmentList.deposit_status=="IA")
            {
                itemView.imageView5.visibility=View.INVISIBLE
                itemView.textView6.visibility=View.INVISIBLE
                itemView.txtAmount.visibility=View.INVISIBLE
                itemView.imageView6.visibility=View.INVISIBLE
                itemView.textView8.visibility=View.INVISIBLE
                itemView.txtMaturityDate.visibility=View.INVISIBLE
                itemView.txtStatus.visibility=View.VISIBLE
                itemView.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                itemView.txtStatus.text=investmentList.deposit_status_txt
                itemView.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_red,0,0,0)
            }
            else if(investmentList.deposit_status=="P")
            {
                itemView.imageView5.visibility=View.INVISIBLE
                itemView.textView6.visibility=View.INVISIBLE
                itemView.txtAmount.visibility=View.INVISIBLE
                itemView.imageView6.visibility=View.INVISIBLE
                itemView.textView8.visibility=View.INVISIBLE
                itemView.txtMaturityDate.visibility=View.INVISIBLE
                itemView.txtStatus.visibility=View.VISIBLE
                itemView.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
                itemView.txtStatus.text=investmentList.deposit_status_txt
                itemView.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_yellow,0,0,0)
            }
            else if(investmentList.deposit_status=="C")
            {

                itemView.imageView5.visibility=View.VISIBLE
                itemView.textView6.visibility=View.VISIBLE
                itemView.txtAmount.visibility=View.VISIBLE
                itemView.imageView6.visibility=View.VISIBLE
                itemView.textView8.visibility=View.VISIBLE
                itemView.txtMaturityDate.visibility=View.VISIBLE
                itemView.txtStatus.visibility=View.INVISIBLE

                itemView.txtAmount.text=investmentList.currency_code+" "+investmentList.maturity_amount.toString()
            }

            else if(investmentList.deposit_status=="A")
            {

                itemView.imageView5.visibility=View.INVISIBLE
                itemView.textView6.visibility=View.INVISIBLE
                itemView.txtAmount.visibility=View.VISIBLE
                itemView.imageView6.visibility=View.VISIBLE
                itemView.textView8.visibility=View.VISIBLE
                itemView.txtMaturityDate.visibility=View.VISIBLE
                itemView.txtStatus.visibility=View.INVISIBLE

                itemView.txtAmount.setTextColor(ContextCompat.getColor(context,R.color.color_deep_green))
                itemView.txtAmount.text=context.getString(R.string.active)
                itemView.txtAmount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved_icon,0,0,0)
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

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}