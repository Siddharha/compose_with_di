package com.app.l_pesa.pin.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonMethod.hideKeyboardView
import com.app.l_pesa.pin.inter.ICallBackPin
import com.app.l_pesa.pin.presenter.PresenterPin
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_change_pin.*
import kotlinx.android.synthetic.main.content_change_pin.*

class ChangePinActivity : AppCompatActivity(), ICallBackPin {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ChangePinActivity)

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

            if(TextUtils.isEmpty(etCurrentPassword.text.toString()))
            {
               hideKeyboardView(this@ChangePinActivity)
               CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.required_password))
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
                    buttonSubmit.isClickable=false
                    swipeRefreshLayout.isRefreshing=true
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("old_pin",etCurrentPassword.text.toString())
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
        swipeRefreshLayout.isRefreshing=false
        onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onFailureChangePin(message: String) {
        buttonSubmit.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,message)
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

    private fun cancelButton()
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if(swipeRefreshLayout.isRefreshing)
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

    override fun onBackPressed() {
        if(swipeRefreshLayout.isRefreshing)
        {
            CommonMethod.customSnackBarError(rootLayout,this@ChangePinActivity,resources.getString(R.string.please_wait))
        }
        else
        {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

    }

}
