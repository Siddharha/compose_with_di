package com.app.l_pesa.login.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
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
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_email_verification.*
import kotlinx.android.synthetic.main.content_email_verification.*
import kotlinx.android.synthetic.main.content_email_verification.rootLayout
import kotlinx.android.synthetic.main.content_pin_set.*

class EmailVerificationActivity : AppCompatActivity(), ICallBackCode, ICallBackDashboard, ICallBackHelp, ICallBackResend {


    private lateinit var  progressDialog   : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)
        setSupportActionBar(toolbar)
        toolbarFont(this@EmailVerificationActivity)

        initLoader()

        btnSubmit.setOnClickListener {

            verifyField()
        }

        etVerificationCode.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        txtResend.paintFlags = txtResend.paintFlags or  Paint.UNDERLINE_TEXT_FLAG
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
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
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
        CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,resources.getString(R.string.sent_otp_via_sms))
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

       /* Handler().postDelayed({
            btnSubmit.isClickable   = true
        }, 1000)*/
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
        val dialogBuilder = AlertDialog.Builder(this@EmailVerificationActivity)
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

    override fun onBackPressed() {
        /*super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)*/
    }
}
