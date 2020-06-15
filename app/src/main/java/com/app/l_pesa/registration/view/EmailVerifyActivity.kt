package com.app.l_pesa.registration.view

import android.app.Activity
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.OnOtpCompletionListener
import kotlinx.android.synthetic.main.activity_email_verify.*
import kotlinx.android.synthetic.main.activity_otp.toolbar

class EmailVerifyActivity : AppCompatActivity() , OnOtpCompletionListener{

    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setContentView(R.layout.activity_email_verify)
        toolbar.title = "Verification code"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@EmailVerifyActivity)

        //
        email = intent.getStringExtra("email")

        otpView.setOtpCompletionListener(this)

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
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onOtpCompleted(otp: String) {
        if (otp.length > 5){
            doVerifyEmail(otp)
        }
    }

    private fun doVerifyEmail(otp: String) {
        CommonMethod.hideKeyboardView(this@EmailVerifyActivity)
        if (CommonMethod.isNetworkAvailable(this@EmailVerifyActivity)){
            
        }
    }
}