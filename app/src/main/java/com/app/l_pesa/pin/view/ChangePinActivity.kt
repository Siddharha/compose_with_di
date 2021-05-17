package com.app.l_pesa.pin.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonMethod.hideKeyboardView
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.pin.inter.ICallBackPin
import com.app.l_pesa.pin.presenter.PresenterPin
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_change_pin.*
import kotlinx.android.synthetic.main.content_change_pin.*

class ChangePinActivity : AppCompatActivity(), ICallBackPin {

    private lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ChangePinActivity)

        cancelButton()
        submitButton()
        initTimer()

    }

    private fun submitButton()
    {
        buttonSubmit.setOnClickListener {

            if(TextUtils.isEmpty(etCurrentPin.text.toString()))
            {
               hideKeyboardView(this@ChangePinActivity)
               CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.required_pin_code))
            }
            else if(etNewPin.text.toString().length!=6)
            {
                hideKeyboardView(this@ChangePinActivity)
                CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.required_pin_code))
            }
            else if(TextUtils.isEmpty(etConfirmNewPin.text.toString()))
            {
                hideKeyboardView(this@ChangePinActivity)
                CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.required_confirm_pin_code))
            }
            else if(etNewPin.text.toString()!=etConfirmNewPin.text.toString())
            {
                hideKeyboardView(this@ChangePinActivity)
                CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.confirm_pin_code_not_match))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(this@ChangePinActivity))
                {
                    val logger = AppEventsLogger.newLogger(this@ChangePinActivity)
                    val params =  Bundle()
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Change Pin")
                    logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

                    progressBar.visibility=View.VISIBLE
                    buttonSubmit.isClickable=false
                    hideKeyboardView(this@ChangePinActivity)
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("old_password",etCurrentPin.text.toString())
                    jsonObject.addProperty("new_pin",etNewPin.text.toString())
                    jsonObject.addProperty("new_c_pin",etConfirmNewPin.text.toString())

                    val presenterPin= PresenterPin()
                    presenterPin.doChangePin(this@ChangePinActivity,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.no_internet))
                }
            }

        }
    }



    override fun onSuccessChangePin() {

        progressBar.visibility=View.INVISIBLE
        buttonSubmit.isClickable=true
        Toast.makeText(this@ChangePinActivity,resources.getString(R.string.pin_change_successfully),Toast.LENGTH_SHORT).show()
        onBackPressed()

    }

    override fun onFailureChangePin(message: String) {

        progressBar.visibility=View.INVISIBLE
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,message)
    }

    override fun onSessionTimeOut(message: String) {

        progressBar.visibility=View.INVISIBLE
        val dialogBuilder = AlertDialog.Builder(this@ChangePinActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@ChangePinActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@ChangePinActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }

    private fun cancelButton()
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
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
                hideKeyboardView(this@ChangePinActivity)
                if(progressBar.visibility==View.VISIBLE && CommonMethod.isNetworkAvailable(this@ChangePinActivity))
                {
                    CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }

            true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun initTimer() {

        countDownTimer= object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }}
        countDownTimer.start()

    }


    override fun onUserInteraction() {
        super.onUserInteraction()

        countDownTimer.cancel()
        countDownTimer.start()
    }


    public override fun onStop() {
        super.onStop()
        countDownTimer.cancel()

    }
    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@ChangePinActivity::class.java.simpleName)

    }

}
