package com.app.l_pesa.otpview.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.OnOtpCompletionListener
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.content_otp.*


class OTPActivity : AppCompatActivity(), OnOtpCompletionListener {

    private lateinit  var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@OTPActivity)

        initLoader()
        otp_view.setOtpCompletionListener(this)

     }

    override fun onOtpCompleted(otp: String) {

        if(otp.length>5)
        {
            progressDialog.show()
        }
    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@OTPActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
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

}
