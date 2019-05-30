package com.app.l_pesa.pin.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod

import kotlinx.android.synthetic.main.activity_set_up_pin.*
import kotlinx.android.synthetic.main.content_set_up_pin.*

class SetUpPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_pin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@SetUpPinActivity)

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

    override fun onBackPressed() {
        if(swipeRefreshLayout.isRefreshing)
        {
            CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.please_wait))
        }
        else
        {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

    }

}
