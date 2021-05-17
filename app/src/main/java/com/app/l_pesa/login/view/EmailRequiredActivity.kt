package com.app.l_pesa.login.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.inter.ICallBackEmail
import com.app.l_pesa.login.presenter.PresenterEmail
import com.app.l_pesa.pinview.model.LoginData
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_email_required.*
import kotlinx.android.synthetic.main.content_email_required.*

class EmailRequiredActivity : AppCompatActivity(), ICallBackEmail {


    private lateinit var  progressDialog   : ProgressDialog
    private val EMAIL_VERIFICATION = 100
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_required)
        setSupportActionBar(toolbar)
        toolbarFont(this@EmailRequiredActivity)

        initLoader()

        btnSubmit.setOnClickListener {

            verifyField()
        }

        val sharedPrefOBJ= SharedPref(this@EmailRequiredActivity)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        etEmail.setText(""+userData.user_personal_info.email_address)
        etEmail.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }


    }

    private fun verifyField()
    {
        hideKeyboard()
        if(TextUtils.isEmpty(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(rootLayout,this@EmailRequiredActivity,resources.getString(R.string.required_email))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@EmailRequiredActivity))
            {
                val logger = AppEventsLogger.newLogger(this@EmailRequiredActivity)
                val params =  Bundle()
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Email Verify")
                logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

                progressDialog.show()

                val jsonObject = JsonObject()
                jsonObject.addProperty("email_address",etEmail.text.toString())

                val presenterEmailOBJ= PresenterEmail()
                presenterEmailOBJ.doVerifyEmail(this@EmailRequiredActivity,jsonObject,this)

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout, this@EmailRequiredActivity, resources.getString(R.string.no_internet))
            }
        }

    }

    private fun hideKeyboard()
    {

        try {
            CommonMethod.hideKeyboardView(this@EmailRequiredActivity)
        } catch (exp: Exception) {

        }

    }
    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@EmailRequiredActivity,R.style.MyAlertDialogStyle)
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

    override fun onSuccessEmail() {
        dismiss()
        startActivity(Intent(this@EmailRequiredActivity, EmailVerificationActivity::class.java))
        overridePendingTransition(R.anim.right_in,R.anim.left_out)
    }

    override fun onErrorEmail(errorMessageOBJ: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@EmailRequiredActivity,errorMessageOBJ)
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

    }
    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@EmailRequiredActivity::class.java.simpleName)

    }
}
