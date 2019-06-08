package com.app.l_pesa.lpk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.lpk.model.ResWithdrawalHistory
import java.text.DecimalFormat


class AdapterWithdrawalHistory (val context: Context, private val listWithdrawalHistory: ArrayList<ResWithdrawalHistory.UserWithdrawalHistory>?) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private lateinit var loadMoreListener: OnLoadMoreListener
    private var isLoading = false
    private var isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0) {
            UserViewHolder(inflater.inflate(R.layout.layout_withdrawal_history, parent, false))
        } else {
            LoadingViewHolder(inflater.inflate(R.layout.layout_load_more, parent, false))
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading) {
            isLoading = true
            loadMoreListener.onLoadMore()
        }

        if (getItemViewType(position) == 0) {
            (holder as UserViewHolder).bindData(context, listWithdrawalHistory!![position])

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listWithdrawalHistory!![position].id != 0) {
            0
        } else {
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

    class UserViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private var txtToken: CommonTextRegular = itemView.findViewById(R.id.txtToken) as CommonTextRegular
        private var txtCreateDate: CommonTextRegular = itemView.findViewById(R.id.txtCreateDate) as CommonTextRegular
        private var txtAddress: CommonTextRegular = itemView.findViewById(R.id.txtAddress) as CommonTextRegular
        private var txtStatus: CommonTextRegular = itemView.findViewById(R.id.txtStatus) as CommonTextRegular
        private var txtReason: CommonTextRegular = itemView.findViewById(R.id.txtReason) as CommonTextRegular
        private var txtRef: CommonTextRegular = itemView.findViewById(R.id.txtRef) as CommonTextRegular
        private var rlAddress: RelativeLayout = itemView.findViewById(R.id.rlAddress) as RelativeLayout
        private var rootConstraint: ConstraintLayout = itemView.findViewById(R.id.rootConstraint) as ConstraintLayout


        @SuppressLint("SetTextI18n", "CheckResult", "SimpleDateFormat")
        fun bindData(context: Context, userWithdrawalHistory: ResWithdrawalHistory.UserWithdrawalHistory) {
            val format = DecimalFormat()
            format.isDecimalSeparatorAlwaysShown = false
            txtRef.text = context.resources.getString(R.string.ref_no) + " " + userWithdrawalHistory.identity_number
            txtToken.text = format.format(userWithdrawalHistory.token_value.toDouble()) + " LPK"
            txtCreateDate.text = CommonMethod.dateConvert(userWithdrawalHistory.created)
            val paddingSize = context.resources.getDimensionPixelSize(R.dimen._5sdp)

            if (userWithdrawalHistory.status =="R") {
                rootConstraint.setPadding(0, 0, 0, paddingSize)
                rlAddress.visibility = View.GONE
                txtReason.visibility = View.GONE
                txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0)
                txtStatus.text = fromHtml(context.resources.getString(R.string.status) + "<font color='#ef3434'>" + " " + userWithdrawalHistory.statusTxt + "</font>")
                txtReason.text = userWithdrawalHistory.reject_reason

                txtStatus.setOnClickListener {

                    if (txtReason.visibility == View.VISIBLE) {
                        txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0)
                        txtReason.visibility = View.GONE
                    } else {
                        txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0)
                        txtReason.visibility = View.VISIBLE
                    }
                }

            } else if (userWithdrawalHistory.status =="P") {
                rootConstraint.setPadding(0, 0, 0, paddingSize)
                rlAddress.visibility = View.GONE
                txtReason.visibility = View.GONE
                txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                txtStatus.text = fromHtml(context.resources.getString(R.string.status) + "<font color='#de970e'>" + " " + userWithdrawalHistory.statusTxt + "</font>")
                txtStatus.isClickable=false
            }
            else if (userWithdrawalHistory.status == "C") {

                rootConstraint.setPadding(0, paddingSize, 0, 0)
                rlAddress.visibility = View.VISIBLE
                txtReason.visibility = View.GONE
                txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                txtStatus.text = fromHtml(context.resources.getString(R.string.status) + "<font color='#00695c'>" + " " + userWithdrawalHistory.statusTxt + "</font>")
                txtAddress.setOnClickListener {

                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(userWithdrawalHistory.real_etherscan_url)
                        context.startActivity(intent)
                    } catch (exp: Exception) {
                    }

                }
            }
            else if (userWithdrawalHistory.status =="F")
            {

                rootConstraint.setPadding(0, paddingSize, 0, 0)
                rlAddress.visibility = View.VISIBLE
                txtReason.visibility = View.GONE
                txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                txtStatus.text = fromHtml(context.resources.getString(R.string.status) + "<font color='#de970e'>" + " " + userWithdrawalHistory.statusTxt + "</font>")

                txtAddress.setOnClickListener {

                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(userWithdrawalHistory.real_etherscan_url)
                        context.startActivity(intent)
                    } catch (exp: Exception) {
                    }

                }
            } else if (userWithdrawalHistory.status=="N")
            {
                rootConstraint.setPadding(0, 0, 0, paddingSize)
                txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                rlAddress.visibility = View.GONE
                txtReason.visibility = View.GONE
                txtStatus.text = fromHtml(context.resources.getString(R.string.status) + "<font color='#00bfa5'>" + " " + userWithdrawalHistory.statusTxt + "</font>")
                txtStatus.isClickable=false
            }

        }

    }


}


    fun fromHtml(source: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(source)
    }
  }



    class LoadingViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

