package com.app.l_pesa.wallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.wallet.model.ResWalletHistory
import java.text.DecimalFormat


class TransactionAllAdapter (val context: Context, private val listTransaction: ArrayList<ResWalletHistory.SavingsHistory>?) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_list_transaction_history, parent, false))
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
            (holder as UserViewHolder).bindData(context, listTransaction!![position])

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(listTransaction!![position].user_id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return listTransaction!!.size
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

            private var txtRef                   : CommonTextRegular = itemView.findViewById(R.id.txtRef) as CommonTextRegular
            private var txtCreateDate            : CommonTextRegular = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular
            private var txtAmount                : CommonTextRegular = itemView.findViewById(R.id.txtAmount) as CommonTextRegular
            private var imageStatus              : ImageView = itemView.findViewById(R.id.imageStatus) as ImageView

        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, savingsHistory: ResWalletHistory.SavingsHistory)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            if(savingsHistory.credit_amount==0.0)
            {
                imageStatus.setImageResource(R.drawable.ic_money_out_side)
                txtAmount.text = savingsHistory.currency_code+" "+format.format(savingsHistory.debit_amount)+" " +context.getString(R.string.dr)
                txtAmount.setTextColor(ContextCompat.getColor(context,R.color.colorRed))

            }
            else
            {
                imageStatus.setImageResource(R.drawable.ic_money_in_side)
                txtAmount.text = savingsHistory.currency_code+" "+format.format(savingsHistory.credit_amount)+" "+context.getString(R.string.cr)
                txtAmount.setTextColor(ContextCompat.getColor(context,R.color.colorApp))

            }

            txtRef.text         = savingsHistory.reference_number
            txtCreateDate.text  = CommonMethod.dateConvert(savingsHistory.created)

        }

    }

    class LoadingViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

}

