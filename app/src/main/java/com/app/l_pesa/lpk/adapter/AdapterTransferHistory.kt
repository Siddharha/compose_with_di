package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.lpk.inter.ICallBackTransferHistory
import com.app.l_pesa.lpk.model.ResTransferHistory
import java.text.DecimalFormat

class AdapterTransferHistory (val context: Context, private val listTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>, private val callBack: ICallBackTransferHistory) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_transfer_history, parent, false))
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
            (holder as UserViewHolder).bindData(context, listTransferHistory[position],callBack)

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(listTransferHistory[position].id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return listTransferHistory.size
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

        var txtRef          : TextView              = itemView.findViewById(R.id.txtRef) as CommonTextRegular
        var txtToken        : TextView              = itemView.findViewById(R.id.txtToken) as CommonTextRegular
        var txtCreateDate   : CommonTextRegular     = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular

        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, userTransferHistory: ResTransferHistory.UserTransferHistory, callBack: ICallBackTransferHistory)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            txtToken.text = format.format(userTransferHistory.tokens.toDouble())+" LPK"
            txtRef.text = context.resources.getString(R.string.ref_no)+" "+userTransferHistory.identity_number
            txtCreateDate.text = CommonMethod.dateConvert(userTransferHistory.created)

        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}