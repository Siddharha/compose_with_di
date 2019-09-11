package com.app.l_pesa.login.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.login.inter.ICallBackEmail
import com.app.l_pesa.login.presenter.PresenterEmail
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_email_required.*
import kotlinx.android.synthetic.main.content_email_required.*

class EmailRequiredActivity : AppCompatActivity(), ICallBackEmail {


    private lateinit var  progressDialog   : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_required)
        setSupportActionBar(toolbar)
        toolbarFont(this@EmailRequiredActivity)

        initLoader()

        btnSubmit.setOnClickListener {

            verifyField()
        }

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
       // btnSubmit.isClickable   = false
        if(TextUtils.isEmpty(etEmail.text.toString()) || !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(rootLayout,this@EmailRequiredActivity,resources.getString(R.string.required_email))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@EmailRequiredActivity))
            {
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

       /* Handler().postDelayed({
            btnSubmit.isClickable   = true
        }, 1000)*/
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
        /*super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)*/
    }
}
