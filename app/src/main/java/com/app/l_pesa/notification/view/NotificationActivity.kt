package com.app.l_pesa.notification.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.notification.adapter.AdapterNotification
import com.app.l_pesa.notification.inter.ICallBackNotification
import com.app.l_pesa.notification.model.ResNotification
import com.app.l_pesa.notification.presenter.PresenterNotification
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.content_notification.*
import java.util.*

class NotificationActivity : AppCompatActivity(), ICallBackNotification {

    private lateinit var countDownTimer: CountDownTimer
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
        initTimer()
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
            llmOBJ.orientation          = RecyclerView.VERTICAL
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

    override fun onSessionTimeOut(message: String) {

        swipeRefreshLayout.isRefreshing = false
        val dialogBuilder = AlertDialog.Builder(this@NotificationActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@NotificationActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@NotificationActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }


    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                CommonMethod.hideKeyboardView(this@NotificationActivity)
                if(swipeRefreshLayout.isRefreshing && CommonMethod.isNetworkAvailable(this@NotificationActivity))
                {
                    CommonMethod.customSnackBarError(rootLayout,this@NotificationActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    private fun initTimer() {

        countDownTimer= object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }}
        countDownTimer.start()

    }


    override fun onUserInteraction() {
        super.onUserInteraction()

        countDownTimer.cancel()
        countDownTimer.start()
    }


    public override fun onStop() {
        super.onStop()
        countDownTimer.cancel()

    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@NotificationActivity::class.java.simpleName)

    }


}
