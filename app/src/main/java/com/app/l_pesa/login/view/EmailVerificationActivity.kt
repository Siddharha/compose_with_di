package com.app.l_pesa.login.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.SingleLineTransformationMethod
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.help.inter.ICallBackHelp
import com.app.l_pesa.help.model.HelpData
import com.app.l_pesa.help.presenter.PresenterHelp
import com.app.l_pesa.login.inter.ICallBackCode
import com.app.l_pesa.login.inter.ICallBackResend
import com.app.l_pesa.login.presenter.PresenterEmail
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_email_verification.*
import kotlinx.android.synthetic.main.content_email_verification.*

class EmailVerificationActivity : AppCompatActivity(), ICallBackCode, ICallBackDashboard, ICallBackHelp, ICallBackResend {


    private lateinit var  progressDialog   : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@EmailVerificationActivity)

        initLoader()

        btnSubmit.setOnClickListener {

            verifyField()
        }

        etVerificationCode.transformationMethod = SingleLineTransformationMethod.getInstance()
        etVerificationCode.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        //txtResend.paintFlags = txtResend.paintFlags or  Paint.UNDERLINE_TEXT_FLAG
        txtResend.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(this@EmailVerificationActivity)) {
                progressDialog.show()
                val presenterEmailOBJ= PresenterEmail()
                presenterEmailOBJ.doResendCode(this@EmailVerificationActivity,this)
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout, this@EmailVerificationActivity, resources.getString(R.string.no_internet))
            }
        }
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@EmailVerificationActivity,R.style.MyAlertDialogStyle)
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

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }
    private fun hideKeyboard()
    {

        try {
            CommonMethod.hideKeyboardView(this@EmailVerificationActivity)
        } catch (exp: Exception) {

        }

    }

    override fun onSuccessResend() {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,resources.getString(R.string.sent_otp_via_email))
    }

    override fun onErrorResend(errorMessageOBJ: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,errorMessageOBJ)
    }


    private fun verifyField()
    {
        hideKeyboard()
        if(etVerificationCode.text.toString().length!=6)
        {
            CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,resources.getString(R.string.six_digit_verification_code_required))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@EmailVerificationActivity))
            {
                val logger = AppEventsLogger.newLogger(this@EmailVerificationActivity)
                val params =  Bundle()
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "OTP Verify")
                logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

                progressDialog.show()

                val jsonObject = JsonObject()
                jsonObject.addProperty("otp",etVerificationCode.text.toString())

                val presenterEmailOBJ= PresenterEmail()
                presenterEmailOBJ.doVerifyCode(this@EmailVerificationActivity,jsonObject,this)

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout, this@EmailVerificationActivity, resources.getString(R.string.no_internet))
            }
        }

    }

    override fun onSuccessVerification() {

        val sharedPref=SharedPref(this@EmailVerificationActivity)
        val presenterDashboard= PresenterDashboard()
        presenterDashboard.getDashboard(this@EmailVerificationActivity,sharedPref.accessToken,this)
    }

    override fun onErrorVerification(errorMessageOBJ: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,errorMessageOBJ)
    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {

        val sharedPrefOBJ=SharedPref(this@EmailVerificationActivity)
        val dashBoardData          = Gson().toJson(data)
        sharedPrefOBJ.userDashBoard       = dashBoardData

        val presenterHelp= PresenterHelp()
        presenterHelp.getHelp(this@EmailVerificationActivity,this)

    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@EmailVerificationActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@EmailVerificationActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@EmailVerificationActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in,R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }

    override fun onFailureDashboard(jsonMessage: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,jsonMessage)

    }

    override fun onSuccessHelp(data: HelpData) {

        val sharedPrefOBJ=SharedPref(this@EmailVerificationActivity)
        val helpSupportData      = Gson().toJson(data)
        sharedPrefOBJ.helpSupport       = helpSupportData
        dismiss()
        val intent = Intent(this@EmailVerificationActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()

    }

    override fun onErrorHelp(message: String) {
        dismiss()
        Toast.makeText(this@EmailVerificationActivity,message, Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@EmailVerificationActivity::class.java.simpleName)

    }
}
