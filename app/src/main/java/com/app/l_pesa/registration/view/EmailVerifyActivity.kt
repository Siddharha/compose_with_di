package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.OnOtpCompletionListener
import com.app.l_pesa.registration.inter.ICallVerifyCode
import com.app.l_pesa.registration.model.Data
import com.app.l_pesa.registration.model.ReqVerifyCode
import com.app.l_pesa.registration.presenter.PresenterRegistrationOne
import kotlinx.android.synthetic.main.activity_email_verify.*
import kotlinx.android.synthetic.main.activity_otp.toolbar

class EmailVerifyActivity : AppCompatActivity() , OnOtpCompletionListener, ICallVerifyCode {

    private var email: String? = null
    private lateinit var  progressDialog   : ProgressDialog

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

        initLoader()

    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@EmailVerifyActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }

    private fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
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
            progressDialog.show()
            val reqVerifyCode = ReqVerifyCode(
                email,otp
            )
            PresenterRegistrationOne().doVerifyOtp(this@EmailVerifyActivity,reqVerifyCode,this)
        }
    }

    override fun onVerificationSuccess(data: Data) {
        dismiss()
        if (data.next == "phone_number"){
            val intent = Intent(this@EmailVerifyActivity,VerifyMobileActivity::class.java)
            intent.putExtra("email",email)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onVerifyFailure(msg: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayoutVerify, this@EmailVerifyActivity, msg)
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




}