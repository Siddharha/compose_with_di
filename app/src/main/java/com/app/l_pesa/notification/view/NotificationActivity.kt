package com.app.l_pesa.notification.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.notification.adapter.AdapterNotification
import com.app.l_pesa.notification.inter.ICallBackNotification
import com.app.l_pesa.notification.model.ResNotification
import com.app.l_pesa.notification.presenter.PresenterNotification
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.content_notification.*
import java.util.ArrayList

class NotificationActivity : AppCompatActivity(), ICallBackNotification {

    private lateinit var listNotificationHistory        : ArrayList<ResNotification.NotificationHistory>
    private lateinit var adapterNotification            : AdapterNotification

    private var hasNext=false
    private var after=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@NotificationActivity)


        initData()
        swipeRefresh()
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
             initData()
        }
    }

    private fun initData()
    {
        listNotificationHistory = ArrayList()
        adapterNotification     = AdapterNotification(this@NotificationActivity, listNotificationHistory)

        if(CommonMethod.isNetworkAvailable(this@NotificationActivity))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterNotification= PresenterNotification()
            presenterNotification.getNotification(this@NotificationActivity,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@NotificationActivity,resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessNotification(notification_history: ArrayList<ResNotification.NotificationHistory>, cursors: ResNotification.Cursors) {

        runOnUiThread {
            cardView.visibility= View.INVISIBLE
            rvList.visibility= View.VISIBLE
            hasNext =cursors.hasNext
            after   =cursors.after
            swipeRefreshLayout.isRefreshing = false
            listNotificationHistory.clear()
            listNotificationHistory.addAll(notification_history)
            adapterNotification         = AdapterNotification(this@NotificationActivity, listNotificationHistory)
            val llmOBJ                  = LinearLayoutManager(this@NotificationActivity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rvList.layoutManager        = llmOBJ
            rvList.adapter              = adapterNotification

            adapterNotification.setLoadMoreListener(object : AdapterNotification.OnLoadMoreListener {
                override fun onLoadMore() {

                    rvList.post {

                        if(hasNext)
                        {
                           loadMore(after)
                        }

                    }

                }
            })

        }
    }

    override fun onSuccessNotificationPaginate(notification_history: ArrayList<ResNotification.NotificationHistory>, cursors: ResNotification.Cursors) {


        hasNext =cursors.hasNext
        after   =cursors.after
        if(listNotificationHistory.size!=0)
        {
            try {

                listNotificationHistory.removeAt(listNotificationHistory.size - 1)
                adapterNotification.notifyDataChanged()
                listNotificationHistory.addAll(notification_history)
                adapterNotification.notifyItemRangeInserted(0, listNotificationHistory.size)

            }
            catch (e:Exception)
            {}
        }
    }

    override fun onEmptyNotification() {

        swipeRefreshLayout.isRefreshing    = false
        rvList.visibility= View.INVISIBLE
        cardView.visibility= View.VISIBLE
    }

    override fun onFailureNotification(message: String) {

        rvList.visibility= View.INVISIBLE
        cardView.visibility= View.INVISIBLE
        swipeRefreshLayout.isRefreshing    = false
        CommonMethod.customSnackBarError(rootLayout,this@NotificationActivity,message)
    }

    private fun loadMore(cursor:String)
    {
        if(CommonMethod.isNetworkAvailable(this@NotificationActivity))
        {
            val notificationModel  = ResNotification.NotificationHistory(0, "", "")
            listNotificationHistory.add(notificationModel)
            adapterNotification.notifyItemInserted(listNotificationHistory.size-1)

            val presenterNotification= PresenterNotification()
            presenterNotification.getNotificationPaginate(this@NotificationActivity,cursor,this)

        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@NotificationActivity,resources.getString(R.string.no_internet))
        }
    }


    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }


}
