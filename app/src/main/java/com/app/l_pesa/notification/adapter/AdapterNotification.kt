package com.app.l_pesa.notification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.notification.model.ResNotification
import kotlinx.android.synthetic.main.layout_list_notification.view.*


class AdapterNotification(val context: Context, private val notificationList: ArrayList<ResNotification.NotificationHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var        loadMoreListener    : OnLoadMoreListener
    private var                 isLoading           = false
    private var                 isMoreDataAvailable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        return if (viewType == 0)
        {
            UserViewHolder(inflater.inflate(R.layout.layout_list_notification, parent, false))
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
            (holder as UserViewHolder).bindData(context, notificationList[position])

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(notificationList[position].id!=0){
            0
        }else{
            1
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
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
        fun  bindData(context: Context, notification: ResNotification.NotificationHistory)
        {

            itemView.txtDate.text=CommonMethod.dateConvert(notification.created)
            itemView.txt_description.text=notification.message

        }

    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}