package com.app.l_pesa.password.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.activity_change_password.*
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_change_password.*
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
import com.app.l_pesa.password.inter.ICallBackPassword
import com.app.l_pesa.password.presenter.PresenterPassword
import com.google.gson.JsonObject


class ChangePasswordActivity : AppCompatActivity(), ICallBackPassword {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarFont(this@ChangePasswordActivity)
        textWatcherPassword()
        initUI()


    }

    private fun initUI()
    {

        etConfirmNewPassword.setOnEditorActionListener { _, actionId, _ ->
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
        hashMapPassword["Password"] = etCurrentPassword.text.toString()

        val hashMapNewPassword:HashMap<String,String> = HashMap()
        hashMapNewPassword["Password"] = etNewPassword.text.toString()

        if(TextUtils.isEmpty(etCurrentPassword.text.toString()))
        {
            hideKeyBoard()
            customSnackBarError(rootLayout,resources.getString(R.string.enter_current_password))
        }
        else if(TextUtils.isEmpty(etNewPassword.text.toString()))
        {
            hideKeyBoard()
            customSnackBarError(rootLayout,resources.getString(R.string.enter_new_password))
        }
        else if(!CommonMethod.passwordRegex(etNewPassword.text.toString()))
        {
            hideKeyBoard()
            customSnackBarError(rootLayout,resources.getString(R.string.enter_password_rule))
        }
        else if(TextUtils.isEmpty(etConfirmNewPassword.text.toString()))
        {
            hideKeyBoard()
            customSnackBarError(rootLayout,resources.getString(R.string.enter_confirm_password))
        }
        else if(etNewPassword.text.toString()!=etConfirmNewPassword.text.toString())
        {
            hideKeyBoard()
            customSnackBarError(rootLayout,resources.getString(R.string.confirm_password_not_match))
        }
        else if (hashMapPassword==(hashMapNewPassword))
        {
            hideKeyBoard()
            customSnackBarError(rootLayout,resources.getString(R.string.new_old_password_different))
        }
        else
        {
            hideKeyBoard()
            buttonSubmit.isClickable    = false
            progressBar.visibility      = View.VISIBLE
            if(CommonMethod.isNetworkAvailable(this@ChangePasswordActivity))
            {
                val jsonObject = JsonObject()
                jsonObject.addProperty("old_password",etCurrentPassword.text.toString())
                jsonObject.addProperty("new_password",etNewPassword.text.toString())
                jsonObject.addProperty("new_c_password",etConfirmNewPassword.text.toString())

                val presenterPassword= PresenterPassword()
                presenterPassword.doResetPassword(this@ChangePasswordActivity,jsonObject,this)
            }
            else
            {
                customSnackBarError(rootLayout,resources.getString(R.string.no_internet))
            }
        }
    }


    private fun hideKeyBoard()
    {
        CommonMethod.hideKeyboardView(this@ChangePasswordActivity)
    }

    private fun customSnackBarError(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }

    private fun customSnackBarSuccess(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimaryLight))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_success, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message

        snackBarOBJ.show()
    }


    private fun textWatcherPassword()
    {
        etNewPassword.addTextChangedListener(textWatcherPasswordRule)

    }

    private val textWatcherPasswordRule = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {

            if(s.length<8)
            {
                imgRuleOne.setImageResource(R.drawable.ic_red_tick)
                txt_rule_one.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleOne.setImageResource(R.drawable.ic_green_tick)
                txt_rule_one.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(CommonMethod.countNumeric(s.toString())==0)
            {
                imgRuleTwo.setImageResource(R.drawable.ic_red_tick)
                txt_rule_two.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleTwo.setImageResource(R.drawable.ic_green_tick)
                txt_rule_two.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(!CommonMethod.hasLowerCase(s.toString()))
            {
                imgRuleThree.setImageResource(R.drawable.ic_red_tick)
                txt_rule_three.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleThree.setImageResource(R.drawable.ic_green_tick)
                txt_rule_three.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(!CommonMethod.hasUpperCase(s.toString()))
            {
                imgRuleFour.setImageResource(R.drawable.ic_red_tick)
                txt_rule_four.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleFour.setImageResource(R.drawable.ic_green_tick)
                txt_rule_four.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(!CommonMethod.hasSymbol(s.toString()))
            {
                imgRuleFive.setImageResource(R.drawable.ic_red_tick)
                txt_rule_five.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleFive.setImageResource(R.drawable.ic_green_tick)
                txt_rule_five.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
        }
    }

    override fun onSuccessResetPassword(message: String) {

        buttonSubmit.isClickable = true
        progressBar.visibility   = View.INVISIBLE
        customSnackBarSuccess(rootLayout,resources.getString(R.string.password_change_success))
        //super.onBackPressed()
        //overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onErrorResetPassword(jsonMessage: String) {
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
                // todo: goto back activity from here

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
