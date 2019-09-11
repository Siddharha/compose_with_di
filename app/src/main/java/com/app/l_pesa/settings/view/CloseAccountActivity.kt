package com.app.l_pesa.settings.view

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import kotlinx.android.synthetic.main.activity_close_account.*
import kotlinx.android.synthetic.main.content_close_account.*

class CloseAccountActivity : AppCompatActivity() {

    private lateinit var  progressDialog   : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_close_account)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@CloseAccountActivity)

        initLoader()
        btnSubmit.setOnClickListener {

            if(TextUtils.isEmpty(etReason.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,this@CloseAccountActivity,"Add Reason")
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(this@CloseAccountActivity))
                {
                    progressDialog.show()

                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@CloseAccountActivity,resources.getString(R.string.no_internet))
                }
            }
        }
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@CloseAccountActivity,R.style.MyAlertDialogStyle)
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
            CommonMethod.hideKeyboardView(this@CloseAccountActivity)
        } catch (exp: Exception) {

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
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }
}
