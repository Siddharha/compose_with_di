package com.app.l_pesa.login.view

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.login.inter.ICallBackCode
import com.app.l_pesa.login.presenter.PresenterEmail
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_email_verification.*
import kotlinx.android.synthetic.main.content_email_verification.*

class EmailVerificationActivity : AppCompatActivity(), ICallBackCode {


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

    private fun verifyField()
    {
        hideKeyboard()
       // btnSubmit.isClickable   = false
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

        dismiss()
    }

    override fun onErrorVerification(errorMessageOBJ: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@EmailVerificationActivity,errorMessageOBJ)
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
