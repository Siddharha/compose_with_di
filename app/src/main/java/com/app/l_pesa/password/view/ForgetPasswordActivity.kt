package com.app.l_pesa.password.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.l_pesa.R
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
        txtSubmit.setOnClickListener {

            val jsonObject = JsonObject()
            jsonObject.addProperty("phone_no",tieMobile.text.toString())
            jsonObject.addProperty("country_code","+91")

            val presenterForgetPassword=PresenterPassword()
            presenterForgetPassword.doForgetPassword(this@ForgetPasswordActivity,jsonObject,this)
        }
    }

    override fun onSuccessResetPassword() {

        Toast.makeText(this@ForgetPasswordActivity,"Success",Toast.LENGTH_SHORT).show()
    }

    override fun onErrorResetPassword(jsonMessage: String) {

        Toast.makeText(this@ForgetPasswordActivity,"Fail",Toast.LENGTH_SHORT).show()
    }
}
