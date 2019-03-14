package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.loanHistory.inter.ICallBackBusinessLoanHistory
import com.app.l_pesa.loanHistory.model.GlobalLoanHistoryModel
import com.app.l_pesa.loanHistory.model.ResLoanHistoryBusiness
import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import kotlinx.android.synthetic.main.layout_loan_history.view.*

class BusinessLoanHistoryAdapter (val context: Context, private val loanHistoryCurrentList: ArrayList<ResLoanHistoryBusiness.LoanHistory>, private val callBackBusiness: ICallBackBusinessLoanHistory) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_loan_history, parent, false))
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
            (holder as UserViewHolder).bindData(context, loanHistoryCurrentList[position],callBackBusiness)

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(loanHistoryCurrentList[position].loan_id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return loanHistoryCurrentList.size
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
        fun  bindData(context: Context, loanHistoryBusiness: ResLoanHistoryBusiness.LoanHistory, callBackCurrent: ICallBackBusinessLoanHistory)
        {

            itemView.txt_loan_no.text=loanHistoryBusiness.identity_number
            itemView.txt_loan_amount.text="$"+loanHistoryBusiness.loan_amount
            itemView.txt_interest_rate.text=loanHistoryBusiness.interest_rate

            when {
                loanHistoryBusiness.loan_status=="A" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.sanctioned_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.approved_on)
                    itemView.txt_status.text = context.resources.getString(R.string.approved)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_deep_green))
                    itemView.img_status.setImageResource(R.drawable.ic_approved_icon)
                }
                loanHistoryBusiness.loan_status=="C" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.finished_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.completed_on)
                    itemView.txt_status.text = context.resources.getString(R.string.completed)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_semi_deep_black))
                    itemView.img_status.setImageResource(R.drawable.ic_approved_icon)

                }
                loanHistoryBusiness.loan_status=="P" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.applied_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.applied_on)
                    itemView.txt_status.text = context.resources.getString(R.string.pending)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
                    itemView.img_status.setImageResource(R.drawable.ic_pending_icon)
                }
                loanHistoryBusiness.loan_status=="DA" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.disapprove_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.disapproved_on)
                    itemView.txt_status.text = context.resources.getString(R.string.disapproved)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    itemView.img_status.setImageResource(R.drawable.ic_disapproved_icon)

                }
                else -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.applied_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.due_on)
                    itemView.txt_status.text = context.resources.getString(R.string.due)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    itemView.img_status.setImageResource(R.drawable.ic_due_icon)
                }
            }


            itemView.rlRoot.setOnClickListener {

                val modelData           = GlobalLoanHistoryModel.getInstance()
                val modelDataBusiness   = ResLoanHistoryCurrent.LoanHistory(loanHistoryBusiness.loan_id,loanHistoryBusiness.identity_number,loanHistoryBusiness.loan_amount,loanHistoryBusiness.interest_rate,
                        loanHistoryBusiness.convertion_dollar_value,loanHistoryBusiness.convertion_loan_amount,loanHistoryBusiness.actual_loan_amount,loanHistoryBusiness.applied_date,
                        loanHistoryBusiness.sanctioned_date,loanHistoryBusiness.finished_date,loanHistoryBusiness.disapprove_date,loanHistoryBusiness.loan_status,loanHistoryBusiness.currency_code,loanHistoryBusiness.due_date,
                        loanHistoryBusiness.duration,loanHistoryBusiness.conversion_charge,loanHistoryBusiness.conversion_charge_amount,loanHistoryBusiness.loan_purpose_message,loanHistoryBusiness.cr_sc_when_requesting_loan,
                        loanHistoryBusiness.processing_fees,loanHistoryBusiness.processing_fees_amount,loanHistoryBusiness.disapprove_reason)

                 modelData.modelData=modelDataBusiness
                callBackCurrent.onClickList()
            }


        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}