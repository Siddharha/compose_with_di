package com.app.l_pesa.password.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.activity_change_login_pin.*
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_change_login_pin.*
import android.text.Editable
import android.text.TextWatcher
import android.graphics.Typeface
import android.widget.TextView
import android.app.Activity
import android.text.TextUtils
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.EditorInfo
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.password.inter.ICallBackLoginPin
import com.app.l_pesa.password.presenter.PresenterPassword
import com.app.l_pesa.settings.inter.ICallBackResetPassword
import com.google.gson.JsonObject


class ChangeLoginPinActivity : AppCompatActivity(), ICallBackLoginPin {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_login_pin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarFont(this@ChangeLoginPinActivity)
        initUI()

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

    private fun customSnackBarSuccess(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ChangeLoginPinActivity,R.color.color_green_success))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_success, null)
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
        customSnackBarSuccess(rootLayout,resources.getString(R.string.pin_change_success))

    }

    override fun onErrorResetPin(jsonMessage: String) {
        buttonSubmit.isClickable    = true
        progressBar.visibility      = View.INVISIBLE
        customSnackBarError(rootLayout,jsonMessage)
    }


    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                CommonMethod.hideKeyboardView(this@ChangeLoginPinActivity)
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

}
