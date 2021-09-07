package com.app.l_pesa.points.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.points.models.CreditPlanHistoryResponse
import com.app.l_pesa.points.inter.ICallBackCreditPlanHistory
import kotlinx.android.synthetic.main.layout_credit_loan_history.view.*
import java.text.DecimalFormat

class PurchaseCreditHistoryListAdapter (val context: Context, private val creditLoanHistoryCurrentList: ArrayList<CreditPlanHistoryResponse.Data.UserBuyPoints>, private val callBackCreditHistory: ICallBackCreditPlanHistory) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_credit_loan_history, parent, false))
        } else {
            LoadingViewHolder(inflater.inflate(R.layout.layout_load_more, parent, false))
        }
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {

        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading)
        {
            isLoading = true
            loadMoreListener.onLoadMore()
        }

        if (getItemViewType(position) == 0) {
            (holder as UserViewHolder).bindData(context, creditLoanHistoryCurrentList[position],callBackCreditHistory,position)

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(creditLoanHistoryCurrentList[position].id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return creditLoanHistoryCurrentList.size
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
        fun  bindData(context: Context, creditPlanHistory: CreditPlanHistoryResponse.Data.UserBuyPoints, iCallBackCreditPlanHistory: ICallBackCreditPlanHistory, position: Int)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false


            itemView.tvTitle.text = creditPlanHistory.bpTitle
            itemView.tvAmount.text =  creditPlanHistory.currencyCode+" " + creditPlanHistory.buyPointAmount.toString()
//            itemView.txt_loan_no.text=loanHistoryBusiness.identity_number
//            itemView.txt_loan_amount.text="$"+format.format(loanHistoryBusiness.loan_amount).toString()
//            itemView.txt_interest_rate.text=loanHistoryBusiness.interest_rate

          /*  when {
                loanHistoryBusiness.loan_status=="A" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.sanctioned_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.approved_on)
                    itemView.txt_status.text = context.resources.getString(R.string.approved)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_deep_green))
                    itemView.img_status.setImageResource(R.drawable.ic_approved_icon)

                    itemView.img_delete.visibility=View.INVISIBLE
                }
                loanHistoryBusiness.loan_status=="C" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.finished_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.completed_on)
                    itemView.txt_status.text = context.resources.getString(R.string.completed)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_semi_deep_black))
                    itemView.img_status.setImageResource(R.drawable.ic_approved_icon)

                    itemView.img_delete.visibility=View.INVISIBLE

                }
                loanHistoryBusiness.loan_status=="P" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.applied_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.applied_on)
                    itemView.txt_status.text = context.resources.getString(R.string.pending)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
                    itemView.img_status.setImageResource(R.drawable.ic_pending_icon)

                    itemView.img_delete.visibility=View.VISIBLE
                }
                loanHistoryBusiness.loan_status=="DA" -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.disapprove_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.disapproved_on)
                    itemView.txt_status.text = context.resources.getString(R.string.disapproved)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    itemView.img_status.setImageResource(R.drawable.ic_disapproved_icon)

                    itemView.img_delete.visibility=View.INVISIBLE

                }
                else -> {
                    itemView.txt_applied_on_date.text=loanHistoryBusiness.applied_date
                    itemView.txt_applied_on.text = context.resources.getString(R.string.due_on)
                    itemView.txt_status.text = context.resources.getString(R.string.due)
                    itemView.txt_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    itemView.img_status.setImageResource(R.drawable.ic_due_icon)

                    itemView.img_delete.visibility=View.INVISIBLE
                }
            }*/


        //    val modelData           = GlobalLoanHistoryModel.getInstance()


       /*     itemView.ll_card_left.setOnClickListener {

                val modelDataBusiness   = ResLoanHistoryCurrent.LoanHistory(loanHistoryBusiness.loan_id,loanHistoryBusiness.identity_number,loanHistoryBusiness.loan_amount,loanHistoryBusiness.interest_rate,
                    loanHistoryBusiness.convertion_dollar_value,loanHistoryBusiness.convertion_loan_amount,loanHistoryBusiness.actual_loan_amount,loanHistoryBusiness.applied_date,
                    loanHistoryBusiness.sanctioned_date,loanHistoryBusiness.finished_date,loanHistoryBusiness.disapprove_date,loanHistoryBusiness.loan_status,loanHistoryBusiness.currency_code,loanHistoryBusiness.due_date,
                    loanHistoryBusiness.duration,loanHistoryBusiness.conversion_charge,loanHistoryBusiness.conversion_charge_amount,loanHistoryBusiness.loan_purpose_message,loanHistoryBusiness.cr_sc_when_requesting_loan,
                    loanHistoryBusiness.processing_fees,loanHistoryBusiness.processing_fees_amount,loanHistoryBusiness.disapprove_reason)

                modelData.modelData=modelDataBusiness
                callBackCurrent.onClickList()
            }

            itemView.rl_card_right.setOnClickListener {

                val modelDataBusiness   = ResLoanHistoryCurrent.LoanHistory(loanHistoryBusiness.loan_id,loanHistoryBusiness.identity_number,loanHistoryBusiness.loan_amount,loanHistoryBusiness.interest_rate,
                    loanHistoryBusiness.convertion_dollar_value,loanHistoryBusiness.convertion_loan_amount,loanHistoryBusiness.actual_loan_amount,loanHistoryBusiness.applied_date,
                    loanHistoryBusiness.sanctioned_date,loanHistoryBusiness.finished_date,loanHistoryBusiness.disapprove_date,loanHistoryBusiness.loan_status,loanHistoryBusiness.currency_code,loanHistoryBusiness.due_date,
                    loanHistoryBusiness.duration,loanHistoryBusiness.conversion_charge,loanHistoryBusiness.conversion_charge_amount,loanHistoryBusiness.loan_purpose_message,loanHistoryBusiness.cr_sc_when_requesting_loan,
                    loanHistoryBusiness.processing_fees,loanHistoryBusiness.processing_fees_amount,loanHistoryBusiness.disapprove_reason)

                modelData.modelData=modelDataBusiness
                callBackCurrent.onClickList()

            }

            itemView.rl_approved_on.setOnClickListener {

                if(loanHistoryBusiness.loan_status=="P")
                {
                    callBackCurrent.onRemoveLoan(position,loanHistoryBusiness)
                }
                else
                {
                    val modelDataBusiness   = ResLoanHistoryCurrent.LoanHistory(loanHistoryBusiness.loan_id,loanHistoryBusiness.identity_number,loanHistoryBusiness.loan_amount,loanHistoryBusiness.interest_rate,
                        loanHistoryBusiness.convertion_dollar_value,loanHistoryBusiness.convertion_loan_amount,loanHistoryBusiness.actual_loan_amount,loanHistoryBusiness.applied_date,
                        loanHistoryBusiness.sanctioned_date,loanHistoryBusiness.finished_date,loanHistoryBusiness.disapprove_date,loanHistoryBusiness.loan_status,loanHistoryBusiness.currency_code,loanHistoryBusiness.due_date,
                        loanHistoryBusiness.duration,loanHistoryBusiness.conversion_charge,loanHistoryBusiness.conversion_charge_amount,loanHistoryBusiness.loan_purpose_message,loanHistoryBusiness.cr_sc_when_requesting_loan,
                        loanHistoryBusiness.processing_fees,loanHistoryBusiness.processing_fees_amount,loanHistoryBusiness.disapprove_reason)

                    modelData.modelData=modelDataBusiness
                    callBackCurrent.onClickList()
                }

            }*/


        }

    }

    class LoadingViewHolder(view: View) :RecyclerView.ViewHolder(view)

}