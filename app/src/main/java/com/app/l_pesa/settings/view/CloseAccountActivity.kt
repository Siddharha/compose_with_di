package com.app.l_pesa.settings.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.*
import android.text.style.BulletSpan
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.auth.core.signin.ui.DisplayUtils.dp
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypefaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.settings.inter.ICallBackCloseAccount
import com.app.l_pesa.settings.presenter.PresenterAccount
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_close_account.*
import kotlinx.android.synthetic.main.content_close_account.*


class CloseAccountActivity : AppCompatActivity(), ICallBackCloseAccount {

    private lateinit var  progressDialog   : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_close_account)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@CloseAccountActivity)

        initLoader()
        initData()
        btnSubmit.setOnClickListener {

            hideKeyboard()
            if(TextUtils.isEmpty(etReason.text.toString().trim()))
            {
                CommonMethod.customSnackBarError(rootLayout,this@CloseAccountActivity,resources.getString(R.string.required_reason_for_close_account))
            }
            else
            {

                val alertDialog = AlertDialog.Builder(this@CloseAccountActivity, R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.close_account_prompt))
                alertDialog.setPositiveButton("YES") { _, _ -> closeAccount() }
                        .setNegativeButton("CANCEL") { dialog, _ ->
                            dialog.dismiss()
                            onBackPressed()
                        }


                alertDialog.show()



            }
        }
    }

    private fun initData()
    {

        val strDeclaration = "Once you close your account, you will be unable to benefit from the fantastic annual interest rates that are amongst the best in the finance industry.\n\n  Without your L-Pesa account, you cannot enjoy the innovative micro-loan services, which enable you to get access to credit within minutes.\n\n Get in touch with one of our customer support representatives to resolve any issues that you may be facing with your account. L-Pesa appreciates every customer, and we are consistently introducing added value features to improve your experience with our services."
        val arr = strDeclaration.split("\n")
        val bulletGap = dp(10)
        val ssb = SpannableStringBuilder()
        for (i in 0 until arr.size) {
            val line = arr[i]
            val ss = SpannableString(line)
            ss.setSpan(BulletSpan(bulletGap), 0, line.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.append(ss)

            if (i + 1 < arr.size)
                ssb.append("\n")

        }

        txt_bullet_one.text = ssb
    }

    private fun closeAccount()
    {
        if(CommonMethod.isNetworkAvailable(this@CloseAccountActivity))
        {
            progressDialog.show()

            val deviceId    = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val jsonObject         = JsonObject()
            jsonObject.addProperty("device_id",deviceId)
            jsonObject.addProperty("reason",etReason.text.toString())

            val presenterAccountOBJ= PresenterAccount()
            presenterAccountOBJ.doCloseAccount(this@CloseAccountActivity,jsonObject,this)

        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@CloseAccountActivity,resources.getString(R.string.no_internet))
        }
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@CloseAccountActivity, R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
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
            CommonMethod.hideKeyboardView(this@CloseAccountActivity)
        } catch (exp: Exception) {

        }

    }

    override fun onSuccessCloseAccount(message: String) {
        dismiss()
        Toast.makeText(this@CloseAccountActivity,message,Toast.LENGTH_LONG).show()
        val sharedPrefOBJ= SharedPref(this@CloseAccountActivity)
        sharedPrefOBJ.removeShared()
        startActivity(Intent(this@CloseAccountActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    override fun onErrorCloseAccount(message: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@CloseAccountActivity,message)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
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
