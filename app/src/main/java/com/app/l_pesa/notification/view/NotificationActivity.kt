package com.app.l_pesa.notification.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView

import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.notification.inter.ICallBackNotification
import com.app.l_pesa.notification.model.ResNotification
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.content_notification.*
import java.util.ArrayList

class NotificationActivity : AppCompatActivity(), ICallBackNotification {


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
        if(CommonMethod.isNetworkAvailable(this@NotificationActivity))
        {

        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@NotificationActivity,resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessNotification(notification_history: ArrayList<ResNotification.NotificationHistory>, cursors: ResNotification.Cursors) {

    }

    override fun onSuccessNotificationPaginate(notification_history: ArrayList<ResNotification.NotificationHistory>, cursors: ResNotification.Cursors) {

    }

    override fun onEmptyNotification() {

    }

    override fun onFailureNotification(message: String) {

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
