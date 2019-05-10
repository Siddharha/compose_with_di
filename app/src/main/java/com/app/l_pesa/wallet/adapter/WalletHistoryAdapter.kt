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
import com.app.l_pesa.common.CustomButtonRegular
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

        private var txtCommission                   : CommonTextRegular    = itemView.findViewById(R.id.txtCommission) as CommonTextRegular
        private var txtCreateDate                   : CommonTextRegular    = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular
        private var txtAmount                       : CommonTextRegular    = itemView.findViewById(R.id.txtAmount) as CommonTextRegular
        private var txtAmountTransfer               : CommonTextRegular    = itemView.findViewById(R.id.txtAmountTransfer) as CommonTextRegular
        private var txtRef                          : CommonTextRegular    = itemView.findViewById(R.id.txtRef) as CommonTextRegular
        private var txtStatus                       : CommonTextRegular    = itemView.findViewById(R.id.txtStatus) as CommonTextRegular

        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, withdrawalHistory: ResWalletWithdrawalHistory.WithdrawalHistory)
        {

            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            txtAmount.text              =  withdrawalHistory.currency_code+" "+format.format(withdrawalHistory.withdrawal_amount)
            txtAmountTransfer.text      =  context.resources.getString(R.string.transfer)+": "+ withdrawalHistory.currency_code+" "+format.format(withdrawalHistory.transfer_amount)
            txtCommission.text          =  context.resources.getString(R.string.commission)+": "+withdrawalHistory.commission_percentage+"%"
            txtRef.text                 =  context.resources.getString(R.string.ref_no)+": "+CommonMethod.dateConvert(withdrawalHistory.identity_number)
            txtCreateDate.text          =  CommonMethod.dateConvert(withdrawalHistory.created)

            if(withdrawalHistory.status==0)
            {
                txtStatus.text = context.resources.getString(R.string.pending)
                txtStatus.setTextColor(ContextCompat.getColor(context,R.color.color_deep_gold))
            }
            else if(withdrawalHistory.status==1)
            {
                txtStatus.text = context.resources.getString(R.string.success)
                txtStatus.setTextColor(ContextCompat.getColor(context,R.color.colorApp))
            }
            else
            {
                txtStatus.text = context.resources.getString(R.string.failure)
                txtStatus.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
            }



        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}