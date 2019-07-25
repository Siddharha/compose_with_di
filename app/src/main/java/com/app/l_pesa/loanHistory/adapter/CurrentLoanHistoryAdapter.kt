package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.loanHistory.inter.ICallBackCurrentLoanHistory
import com.app.l_pesa.loanHistory.model.GlobalLoanHistoryModel
import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import kotlinx.android.synthetic.main.layout_loan_history.view.*
import java.text.DecimalFormat

class CurrentLoanHistoryAdapter (val context: Context, private val loanHistoryCurrentList: ArrayList<ResLoanHistoryCurrent.LoanHistory>, private val callBackCurrent: ICallBackCurrentLoanHistory) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
            (holder as UserViewHolder).bindData(context, loanHistoryCurrentList[position],callBackCurrent,position)

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
        fun  bindData(context: Context, loanHistoryCurrent: ResLoanHistoryCurrent.LoanHistory, callBackCurrent: ICallBackCurrentLoanHistory, position: Int)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            itemView.txt_loan_no.text=loanHistoryCurrent.identity_number
            itemView.txt_loan_amount.text="$"+format.format(loanHistoryCurrent.loan_amount).toString()
            itemView.txt_interest_rate.text=loanHistoryCurrent.interest_rate

            when {
                loanHistoryCurrent.loan_status=="A" -> {
                    itemView.txt_applied_on_date.text=loanHistoryCurrent.sanctioned_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.approved_on)
                    itemView.txt_status.text = context.resources.getString(R.string.approved)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_deep_green))
                    itemView.img_status.setImageResource(R.drawable.ic_approved_icon)

                    itemView.img_delete.visibility=View.INVISIBLE
                }
                loanHistoryCurrent.loan_status=="C" -> {
                    itemView.txt_applied_on_date.text=loanHistoryCurrent.finished_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.completed_on)
                    itemView.txt_status.text = context.resources.getString(R.string.completed)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_semi_deep_black))
                    itemView.img_status.setImageResource(R.drawable.ic_approved_icon)

                    itemView.img_delete.visibility=View.INVISIBLE

                }
                loanHistoryCurrent.loan_status=="P" -> {
                    itemView.txt_applied_on_date.text=loanHistoryCurrent.applied_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.applied_on)
                    itemView.txt_status.text = context.resources.getString(R.string.pending)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
                    itemView.img_status.setImageResource(R.drawable.ic_pending_icon)

                    itemView.img_delete.visibility=View.VISIBLE
                }
                loanHistoryCurrent.loan_status=="DA" -> {
                    itemView.txt_applied_on_date.text=loanHistoryCurrent.disapprove_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.disapproved_on)
                    itemView.txt_status.text = context.resources.getString(R.string.disapproved)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    itemView.img_status.setImageResource(R.drawable.ic_disapproved_icon)

                    itemView.img_delete.visibility=View.INVISIBLE

                }
                else -> {
                    itemView.txt_applied_on_date.text=loanHistoryCurrent.applied_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.due_on)
                    itemView.txt_status.text = context.resources.getString(R.string.due)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    itemView.img_status.setImageResource(R.drawable.ic_due_icon)

                    itemView.img_delete.visibility=View.INVISIBLE
                }
            }




            itemView.ll_card_left.setOnClickListener {

                val modelData=GlobalLoanHistoryModel.getInstance()
                modelData.modelData=loanHistoryCurrent
                callBackCurrent.onClickList()
            }

            itemView.rl_card_right.setOnClickListener {

                val modelData=GlobalLoanHistoryModel.getInstance()
                modelData.modelData=loanHistoryCurrent
                callBackCurrent.onClickList()

            }

            itemView.rl_approved_on.setOnClickListener {

                if(loanHistoryCurrent.loan_status=="P")
                {
                    callBackCurrent.onRemoveLoan(position,loanHistoryCurrent)
                }
                else
                {
                    val modelData=GlobalLoanHistoryModel.getInstance()
                    modelData.modelData=loanHistoryCurrent
                    callBackCurrent.onClickList()
                }

            }


        }



    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}