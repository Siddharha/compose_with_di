package com.app.l_pesa.loanHistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.loanHistory.model.ResLoanHistory

class CurrentLoanHistoryAdapter (val context: Context, private val loanHistoryList: ArrayList<ResLoanHistory.LoanHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    var                         isLoading           = false
    private var                 isMoreDataAvailable = true


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading)
        {
            isLoading = true
            loadMoreListener.onLoadMore()
        }

        if (getItemViewType(position) == 0) {
            (holder as UserViewHolder).bindData(context, loanHistoryList[position])

        }

    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtLoanNo       : CommonTextRegular = itemView.findViewById(R.id.txt_loan_no)

        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, loanHistory: ResLoanHistory.LoanHistory )
        {

            txtLoanNo.text = loanHistory.actual_loan_amount
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(loanHistoryList[position].loan_id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int = loanHistoryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_loan_history, parent, false))
        } else {
            LoadingViewHolder(inflater.inflate(R.layout.layout_load_more, parent, false))
        }
    }


     inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

         val progressBar       : ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bindUser(userName: String)
        {

        }
    }


    fun setMoreDataAvailable(moreDataAvailable: Boolean) {
        isMoreDataAvailable = moreDataAvailable

    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
        isLoading = false
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        this.loadMoreListener = loadMoreListener
    }
}