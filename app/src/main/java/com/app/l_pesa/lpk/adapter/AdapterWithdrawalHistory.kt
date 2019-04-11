package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.lpk.model.ResWithdrawalHistory
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import java.lang.Exception


class AdapterWithdrawalHistory (val context: Context, private val listWithdrawalHistory: ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_withdrawal_history, parent, false))
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

        private var txtReqToken        : CommonTextRegular = itemView.findViewById(R.id.txtReqToken) as CommonTextRegular
        private var txtStatus          : CommonTextRegular = itemView.findViewById(R.id.txtStatus) as CommonTextRegular
        private var txtCreateDate      : CommonTextRegular = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular
        private var txtReason          : CommonTextRegular = itemView.findViewById(R.id.txtReason) as CommonTextRegular
        private var buttonStatus       : CustomButtonRegular = itemView.findViewById(R.id.buttonStatus) as CustomButtonRegular


        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun  bindData(context: Context, userWithdrawalHistory: ResWithdrawalHistory.UserWithdrawalHistory)
        {
            txtReqToken.text = context.getString(R.string.request_token)+": "+userWithdrawalHistory.token_value
            txtCreateDate.text = CommonMethod.dateConvert(userWithdrawalHistory.created)

            when {
                userWithdrawalHistory.status=="R" -> {

                    buttonStatus.visibility=View.INVISIBLE
                    txtReason.visibility=View.GONE
                    txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0)
                    txtStatus.text=fromHtml(context.resources.getString(R.string.status)+"<font color='#D2322D'>"+" "+userWithdrawalHistory.statusTxt+"</font>")
                    txtReason.text=userWithdrawalHistory.reject_reason

                    txtStatus.setOnClickListener {

                        if(txtReason.visibility==View.VISIBLE)
                        {
                            txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0)
                            txtReason.visibility=View.GONE
                        }
                        else
                        {
                            txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0)
                            txtReason.visibility=View.VISIBLE
                        }
                    }

                }
                userWithdrawalHistory.status=="P" -> {
                    buttonStatus.visibility=View.INVISIBLE
                    txtReason.visibility=View.GONE
                    txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    txtStatus.text=fromHtml(context.resources.getString(R.string.status)+"<font color='#4e6485'>"+" "+userWithdrawalHistory.statusTxt+"</font>")
                }
                userWithdrawalHistory.status=="C" -> {
                    buttonStatus.visibility=View.VISIBLE
                    txtReason.visibility=View.GONE
                    txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    txtStatus.text=fromHtml(context.resources.getString(R.string.status)+"<font color='#00695c'>"+" "+userWithdrawalHistory.statusTxt+"</font>")
                    buttonStatus.setOnClickListener {

                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(userWithdrawalHistory.real_etherscan_url)
                            context.startActivity(intent)
                        }
                        catch (exp:Exception)
                        {}

                    }
                }

                userWithdrawalHistory.status=="F" -> {

                    buttonStatus.visibility=View.VISIBLE
                    txtReason.visibility=View.GONE
                    txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    txtStatus.text=fromHtml(context.resources.getString(R.string.status)+"<font color='#de970e'>"+" "+userWithdrawalHistory.statusTxt+"</font>")

                    buttonStatus.setOnClickListener {

                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(userWithdrawalHistory.real_etherscan_url)
                            context.startActivity(intent)
                        }
                        catch (exp:Exception)
                        {}

                    }
                }

                userWithdrawalHistory.status=="N" -> {

                    buttonStatus.visibility=View.INVISIBLE
                    txtReason.visibility=View.GONE
                    txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    txtStatus.text=fromHtml(context.resources.getString(R.string.status)+"<font color='#00bfa5'>"+" "+userWithdrawalHistory.statusTxt+"</font>")

                }
                else -> {

                }
            }


        }

        private fun fromHtml(source: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(source)
            }
        }

    }



    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}