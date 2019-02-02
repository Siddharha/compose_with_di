package com.app.l_pesa.password.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.password.inter.ICallBackPassword
import com.app.l_pesa.password.presenter.PresenterPassword
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : AppCompatActivity(), ICallBackPassword {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        forgetPassword()
    }


    private fun forgetPassword()
    {

        tieEmail.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        txtSubmit.setOnClickListener {
            verifyField()

        }
    }

    private fun verifyField()
    {
        CommonMethod.hideKeyboard(this@ForgetPasswordActivity)
        if(tieMobile.text.toString().length<10 && !CommonMethod.isValidEmailAddress(tieEmail.text.toString()))
        {
            CommonMethod.setSnackBar(this@ForgetPasswordActivity,ll_root,resources.getString(R.string.required_phone_email))
        }
        else if(!TextUtils.isEmpty(tieMobile.text.toString()) && !TextUtils.isEmpty(tieEmail.text.toString()))
        {
             CommonMethod.setSnackBar(this@ForgetPasswordActivity,ll_root,resources.getString(R.string.required_phone_email))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@ForgetPasswordActivity))
            {
                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",tieMobile.text.toString())
                jsonObject.addProperty("country_code","+91")

                val presenterForgetPassword=PresenterPassword()
                presenterForgetPassword.doForgetPassword(this@ForgetPasswordActivity,jsonObject,this)
            }
            else
            {
                CommonMethod.setSnackBar(this@ForgetPasswordActivity,ll_root,resources.getString(R.string.no_internet))
            }
        }

    }

    override fun onSuccessResetPassword() {

        Toast.makeText(this@ForgetPasswordActivity,"Success",Toast.LENGTH_SHORT).show()
    }

    override fun onErrorResetPassword(jsonMessage: String) {

        Toast.makeText(this@ForgetPasswordActivity,"Fail",Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {

        val intent = Intent(this@ForgetPasswordActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }
}
