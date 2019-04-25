package com.app.l_pesa.wallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
import java.text.DecimalFormat

class WalletHistoryAdapter(val context: Context, private val listWithdrawalHistory: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_list_wallet_history, parent, false))
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
            (holder as UserViewHolder).bindData(context, listWithdrawalHistory!![position])

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(listWithdrawalHistory!![position].id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return listWithdrawalHistory!!.size
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

        private var txtRef                   : CommonTextRegular = itemView.findViewById(R.id.txtRef) as CommonTextRegular
        private var txtCreateDate            : CommonTextRegular = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular
        private var txtAmount                : CommonTextRegular = itemView.findViewById(R.id.txtAmount) as CommonTextRegular
        private var txtAmountStatus          : CommonTextRegular = itemView.findViewById(R.id.txtAmountStatus) as CommonTextRegular
        private var imageStatus              : ImageView = itemView.findViewById(R.id.imageStatus) as ImageView

        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, withdrawalHistory: ResWalletWithdrawalHistory.WithdrawalHistory)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            txtRef.text         = withdrawalHistory.identity_number
           // txtCreateDate.text  = CommonMethod.dateConvert(savingsHistory.created)

        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}