package com.app.l_pesa.pin.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonMethod.hideKeyboardView
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.pin.inter.ICallBackSetPin
import com.app.l_pesa.pin.presenter.PresenterPin
import com.app.l_pesa.pinview.model.LoginData
import com.google.gson.Gson
import com.google.gson.JsonObject

import kotlinx.android.synthetic.main.activity_set_up_pin.*
import kotlinx.android.synthetic.main.content_set_up_pin.*

class SetUpPinActivity : AppCompatActivity(), ICallBackSetPin {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_pin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@SetUpPinActivity)

        swipeRefresh()
        cancelButton()
        submitButton()
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun submitButton()
    {
        buttonSubmit.setOnClickListener {

            if(etNewPin.text.toString().length!=6)
            {
                hideKeyboardView(this@SetUpPinActivity)
                CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.required_pin_code))
            }
            else if(TextUtils.isEmpty(etConfirmNewPin.text.toString()))
            {
                hideKeyboardView(this@SetUpPinActivity)
                CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.required_confirm_pin_code))
            }
            else if(etNewPin.text.toString()!=etConfirmNewPin.text.toString())
            {
                hideKeyboardView(this@SetUpPinActivity)
                CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.confirm_pin_code_not_match))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(this@SetUpPinActivity))
                {
                    buttonSubmit.isClickable=false
                    hideKeyboardView(this@SetUpPinActivity)
                    swipeRefreshLayout.isRefreshing=true
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("new_pin",etNewPin.text.toString())
                    jsonObject.addProperty("new_c_pin",etConfirmNewPin.text.toString())

                    val presenterPin= PresenterPin()
                    presenterPin.doSetUpPin(this@SetUpPinActivity,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.no_internet))
                }
            }

        }
    }

    override fun onSuccessSetPin() {

        val sharedPrefOBJ= SharedPref(this@SetUpPinActivity)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        userData.user_info.mpin_password=true
        val json = Gson().toJson(userData)
        sharedPrefOBJ.userInfo = json
        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        Toast.makeText(this@SetUpPinActivity,resources.getString(R.string.pin_create_successfully),Toast.LENGTH_SHORT).show()

        val intent = Intent(this@SetUpPinActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)

    }

    override fun onFailureSetPin(message: String) {
        buttonSubmit.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,message)
    }

    override fun onSessionTimeOut(message: String) {
        swipeRefreshLayout.isRefreshing=false

        val dialogBuilder = AlertDialog.Builder(this@SetUpPinActivity)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@SetUpPinActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@SetUpPinActivity, MainActivity::class.java))
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
                if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    hideKeyboardView(this@SetUpPinActivity)
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if(swipeRefreshLayout.isRefreshing)
        {
            CommonMethod.customSnackBarError(rootLayout,this@SetUpPinActivity,resources.getString(R.string.please_wait))
        }
        else
        {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

    }

}
