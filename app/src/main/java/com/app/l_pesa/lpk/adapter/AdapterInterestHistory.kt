package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.lpk.model.ResInterestHistory

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

        var txtTokenValue          : CommonTextRegular = itemView.findViewById(R.id.txtTokenValue) as CommonTextRegular
        var txtAmount              : CommonTextRegular = itemView.findViewById(R.id.txtAmount) as CommonTextRegular
        var txtNarration           : CommonTextRegular = itemView.findViewById(R.id.txtNarration) as CommonTextRegular
        var txtRef                 : CommonTextRegular = itemView.findViewById(R.id.txtRef) as CommonTextRegular


        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, userInterestHistory: ResInterestHistory.UserInterestHistory)
        {
            txtTokenValue.text = context.getString(R.string.actual_token_values)+":"+userInterestHistory.tokens
            txtAmount.text = userInterestHistory.currency_code+" "+userInterestHistory.amount
            txtNarration.text = context.getString(R.string.narration)+":"+userInterestHistory.narration
            txtRef.text = context.getString(R.string.ref_no)+userInterestHistory.identity_number

        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}

