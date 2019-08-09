package com.app.l_pesa.pin.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.pin.inter.ICallBackLoginPin
import com.app.l_pesa.pin.presenter.PresenterPassword
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_change_login_pin.*
import kotlinx.android.synthetic.main.content_change_login_pin.*


class ChangeLoginPinActivity : AppCompatActivity(), ICallBackLoginPin {

    private lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_login_pin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarFont(this@ChangeLoginPinActivity)
        initUI()
        initTimer()

    }

    private fun initUI()
    {

        etConfirmNewPin.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doValidate()
                handled = true
            }
            handled
        }
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }

        buttonSubmit.setOnClickListener {
            doValidate()

        }
    }

    private fun doValidate()
    {
        val hashMapPassword:HashMap<String,String> = HashMap()
        hashMapPassword["Pin"] = etCurrentPin.text.toString()

        val hashMapNewPassword:HashMap<String,String> = HashMap()
        hashMapNewPassword["Pin"] = etNewPin.text.toString()

        hideKeyBoard()
        if(TextUtils.isEmpty(etCurrentPin.text.toString()))
        {

            customSnackBarError(rootLayout,resources.getString(R.string.required_current_pin))
        }
        else if(TextUtils.isEmpty(etNewPin.text.toString()))
        {

            customSnackBarError(rootLayout,resources.getString(R.string.required_new_pin))
        }
        else if(TextUtils.isEmpty(etConfirmNewPin.text.toString()))
        {

            customSnackBarError(rootLayout,resources.getString(R.string.required_confirm_pin))
        }
        else if(etNewPin.text.toString()!=etConfirmNewPin.text.toString())
        {

            customSnackBarError(rootLayout,resources.getString(R.string.confirm_pin_not_match))
        }
        else if (hashMapPassword==(hashMapNewPassword))
        {

            customSnackBarError(rootLayout,resources.getString(R.string.old_new_password_different))
        }
        else
        {

            buttonSubmit.isClickable    = false
            progressBar.visibility      = View.VISIBLE
            if(CommonMethod.isNetworkAvailable(this@ChangeLoginPinActivity))
            {
                val jsonObject = JsonObject()
                jsonObject.addProperty("old_pin",etCurrentPin.text.toString())
                jsonObject.addProperty("new_pin",etNewPin.text.toString())
                jsonObject.addProperty("new_c_pin",etConfirmNewPin.text.toString())

                val presenterPassword= PresenterPassword()
                presenterPassword.doChangeLoginPin(this@ChangeLoginPinActivity,jsonObject,this)
            }
            else
            {
                customSnackBarError(rootLayout,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun hideKeyBoard()
    {
        CommonMethod.hideKeyboardView(this@ChangeLoginPinActivity)
    }

    private fun customSnackBarError(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ChangeLoginPinActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message

        snackBarOBJ.show()
    }


    override fun onSuccessResetPin(message: String) {

        etCurrentPin.text!!.clear()
        etNewPin.text!!.clear()
        etConfirmNewPin.text!!.clear()
        buttonSubmit.isClickable = true
        progressBar.visibility   = View.INVISIBLE
        Toast.makeText(this@ChangeLoginPinActivity,resources.getString(R.string.pin_change_success),Toast.LENGTH_SHORT).show()
        onBackPressed()

    }

    override fun onErrorResetPin(jsonMessage: String) {
        buttonSubmit.isClickable    = true
        progressBar.visibility      = View.INVISIBLE
        customSnackBarError(rootLayout,jsonMessage)
    }

    override fun onSessionTimeOut(message: String) {

        progressBar.visibility      = View.INVISIBLE
        val dialogBuilder = AlertDialog.Builder(this@ChangeLoginPinActivity)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@ChangeLoginPinActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@ChangeLoginPinActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

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
                CommonMethod.hideKeyboardView(this@ChangeLoginPinActivity)
                if(progressBar.visibility==View.VISIBLE && CommonMethod.isNetworkAvailable(this@ChangeLoginPinActivity))
                {
                    CommonMethod.customSnackBarError(rootLayout,this@ChangeLoginPinActivity,resources.getString(R.string.please_wait))
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



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
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

}
