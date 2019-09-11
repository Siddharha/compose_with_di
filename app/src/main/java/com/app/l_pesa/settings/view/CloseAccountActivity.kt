package com.app.l_pesa.settings.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
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
        btnSubmit.setOnClickListener {

            hideKeyboard()
            if(TextUtils.isEmpty(etReason.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,this@CloseAccountActivity,"Add Reason")
            }
            else
            {

                val alertDialog = AlertDialog.Builder(this@CloseAccountActivity)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.close_account_prompt))
                alertDialog.setPositiveButton("Yes") { _, _ -> closeAccount() }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()

            }
        }
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

            //println("JSON"+jsonObject)
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

    override fun onSuccessCloseAccount() {
        dismiss()
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
