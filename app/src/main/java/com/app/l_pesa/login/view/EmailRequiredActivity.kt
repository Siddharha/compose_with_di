package com.app.l_pesa.login.view

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod

import kotlinx.android.synthetic.main.activity_email_required.*
import kotlinx.android.synthetic.main.content_email_required.*

class EmailRequiredActivity : AppCompatActivity() {

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
        if(TextUtils.isEmpty(etEmail.text.toString()) || !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(rootLayout,this@EmailRequiredActivity,resources.getString(R.string.required_email))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@EmailRequiredActivity))
            {
                progressDialog.show()

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
