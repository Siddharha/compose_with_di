package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.lpk.model.ResInterestHistory
import java.text.DecimalFormat

class AdapterInterestHistory (val context: Context, private val listInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_interest_history, parent, false))
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
            (holder as UserViewHolder).bindData(context, listInterestHistory!![position])

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(listInterestHistory!![position].user_id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return listInterestHistory!!.size
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

        private var txtTokenValue          : CommonTextRegular = itemView.findViewById(R.id.txtTokenValue) as CommonTextRegular
        private var txtAmount              : CommonTextRegular = itemView.findViewById(R.id.txtAmount) as CommonTextRegular
        private var txtRef                 : CommonTextRegular = itemView.findViewById(R.id.txtRef) as CommonTextRegular
        private var txtCreateDate          : CommonTextRegular = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular


        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, userInterestHistory: ResInterestHistory.UserInterestHistory)
        {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false

            if(userInterestHistory.amount=="0.00")
            {
                txtAmount.text = context.getString(R.string.cr)+": "+userInterestHistory.interest_token+" LPK"
            }
            else
            {
                txtAmount.text = context.getString(R.string.cr)+": "+userInterestHistory.currency_code+" "+format.format(userInterestHistory.amount.toDouble()).toString()
            }

            txtTokenValue.text = format.format(userInterestHistory.actual_tokens.toDouble()).toString()+" LPK"
            txtRef.text = context.getString(R.string.ref_no)+" "+userInterestHistory.identity_number
            txtCreateDate.text = CommonMethod.dateConvert(userInterestHistory.created)

        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}

