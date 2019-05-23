package com.app.l_pesa.pinview.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.login.model.PostData
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.pinview.inter.ICallBackPinSet
import com.app.l_pesa.pinview.model.LoginData
import com.app.l_pesa.pinview.presenter.PresenterPinSet
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_pin_set.*
import kotlinx.android.synthetic.main.content_pin_set.*


class PinSetActivity : AppCompatActivity(), ICallBackPinSet, ICallBackDashboard {

    private lateinit  var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_set)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@PinSetActivity)
        initLoader()
        initData()


    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@PinSetActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }
    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun initData()
    {
        val font = ResourcesCompat.getFont(this@PinSetActivity, R.font.montserrat)
        pass_code_view.setTypeFace(font)

        val sharedPrefOBJ= SharedPref(this@PinSetActivity)
        val modelDevice = Gson().fromJson<PostData>(sharedPrefOBJ.deviceInfo, PostData::class.java)


        pass_code_view.setOnTextChangeListener { text ->
            if (text.length>5) {

                if(CommonMethod.isNetworkAvailable(this@PinSetActivity))
                {
                    progressDialog.show()
                    val jsonObjectDeviceInfo = JsonObject()
                    jsonObjectDeviceInfo.addProperty("phone_no", modelDevice.phone_no)
                    jsonObjectDeviceInfo.addProperty("country_code", modelDevice.country_code)
                    jsonObjectDeviceInfo.addProperty("platform_type", modelDevice.platform_type)
                    jsonObjectDeviceInfo.addProperty("device_token", modelDevice.device_token)

                    val jsonObjectDeviceData = JsonObject()
                    jsonObjectDeviceData.addProperty("device_id", modelDevice.device_data.device_id)
                    jsonObjectDeviceData.addProperty("sdk",modelDevice.device_data.sdk)
                    jsonObjectDeviceData.addProperty("imei",modelDevice.device_data.imei)
                    jsonObjectDeviceData.addProperty("imsi",modelDevice.device_data.imsi)
                    jsonObjectDeviceData.addProperty("simSerial_no",modelDevice.device_data.simSerial_no)
                    jsonObjectDeviceData.addProperty("sim_operator_Name",modelDevice.device_data.sim_operator_Name)
                    jsonObjectDeviceData.addProperty("screen_height",modelDevice.device_data.screen_height)
                    jsonObjectDeviceData.addProperty("screen_width",modelDevice.device_data.screen_width)
                    jsonObjectDeviceData.addProperty("device", modelDevice.device_data.device)
                    jsonObjectDeviceData.addProperty("model", modelDevice.device_data.model)
                    jsonObjectDeviceData.addProperty("product", modelDevice.device_data.product)
                    jsonObjectDeviceData.addProperty("manufacturer", modelDevice.device_data.manufacturer)

                    jsonObjectDeviceInfo.add("device_data",jsonObjectDeviceData)

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("phone_number",modelDevice.country_code+modelDevice.phone_no)
                    jsonObject.addProperty("apps_pin",pass_code_view.passCodeText)
                    jsonObject.add("device_info",jsonObjectDeviceInfo)

                     println("REQUEST"+jsonObject.toString())


                     val presenterPinSet= PresenterPinSet()
                     presenterPinSet.dosetPin(this@PinSetActivity,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@PinSetActivity,resources.getString(R.string.no_internet))
                    pass_code_view.setError(true)
                }

            }
        }
    }

    override fun onSuccessPinSet(data: LoginData) {

        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        sharedPrefOBJ.accessToken   =data.access_token
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPrefOBJ.userInfo      = json
        sharedPrefOBJ.userCreditScore=data.user_info.credit_score.toString()

        val presenterDashboard= PresenterDashboard()
        presenterDashboard.getDashboard(this@PinSetActivity,data.access_token,this)


    }

    override fun onErrorPinSet(message: String) {

        pass_code_view.setError(true)
        pass_code_view.reset()
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@PinSetActivity,message)
    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {

        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        val gson                          = Gson()
        val dashBoardData                 = gson.toJson(data)
        sharedPrefOBJ.userDashBoard       = dashBoardData

        val intent = Intent(this@PinSetActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    override fun onFailureDashboard(jsonMessage: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@PinSetActivity,jsonMessage)
    }

    override fun onSessionTimeOut(jsonMessage: String) {
        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@PinSetActivity)
        dialogBuilder.setMessage(jsonMessage)
                // if the dialog is cancelable
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@PinSetActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@PinSetActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                })

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont =   Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text ==    toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onBackPressed() {

        val intent = Intent(this@PinSetActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

}
