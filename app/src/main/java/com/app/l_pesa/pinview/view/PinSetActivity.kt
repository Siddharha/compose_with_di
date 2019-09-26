package com.app.l_pesa.pinview.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypefaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.help.inter.ICallBackHelp
import com.app.l_pesa.help.model.HelpData
import com.app.l_pesa.help.presenter.PresenterHelp
import com.app.l_pesa.login.view.EmailRequiredActivity
import com.app.l_pesa.login.view.EmailVerificationActivity
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.otpview.model.PinData
import com.app.l_pesa.pinview.inter.ICallBackPinSet
import com.app.l_pesa.pinview.model.LoginData
import com.app.l_pesa.pinview.presenter.PresenterPinSet
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_pin_set.*
import kotlinx.android.synthetic.main.content_pin_set.*

class PinSetActivity : AppCompatActivity(), ICallBackPinSet, ICallBackDashboard, ICallBackHelp  {


    private lateinit var  progressDialog   : ProgressDialog

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
        progressDialog = ProgressDialog(this@PinSetActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
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

    private fun initData()
    {
        val font = ResourcesCompat.getFont(this@PinSetActivity,R.font.montserrat)
        pass_code_view.setTypeFace(font)
        pass_code_view.setKeyTextColor(ContextCompat.getColor(this@PinSetActivity, R.color.colorApp))

        pass_code_view.setOnTextChangeListener(PassCodeView.TextChangeListener { text ->
            if (text.length == 6) {

                val sharedPrefOBJ= SharedPref(this@PinSetActivity)
                val modelDevice = Gson().fromJson<PinData>(sharedPrefOBJ.deviceInfo, PinData::class.java)

                if (CommonMethod.isNetworkAvailable(this@PinSetActivity))
                {
                    progressDialog.show()
                    val jsonObjectDeviceInfo = JsonObject()
                    jsonObjectDeviceInfo.addProperty("phone_no", modelDevice.post_data.phone_no)
                    jsonObjectDeviceInfo.addProperty("country_code", modelDevice.post_data.country_code)
                    jsonObjectDeviceInfo.addProperty("platform_type", modelDevice.post_data.platform_type)
                    jsonObjectDeviceInfo.addProperty("device_token", modelDevice.post_data.device_token)

                    val jsonObjectDeviceData = JsonObject()
                    jsonObjectDeviceData.addProperty("device_id", modelDevice.post_data.device_data.device_id)
                    jsonObjectDeviceData.addProperty("sdk", modelDevice.post_data.device_data.sdk)
                    jsonObjectDeviceData.addProperty("imei", modelDevice.post_data.device_data.imei)
                    jsonObjectDeviceData.addProperty("imsi", modelDevice.post_data.device_data.imsi)
                    jsonObjectDeviceData.addProperty("simSerial_no", modelDevice.post_data.device_data.simSerial_no)
                    jsonObjectDeviceData.addProperty("sim_operator_Name", modelDevice.post_data.device_data.sim_operator_Name)
                    jsonObjectDeviceData.addProperty("screen_height", modelDevice.post_data.device_data.screen_height)
                    jsonObjectDeviceData.addProperty("screen_width", modelDevice.post_data.device_data.screen_width)
                    jsonObjectDeviceData.addProperty("device", modelDevice.post_data.device_data.device)
                    jsonObjectDeviceData.addProperty("model", modelDevice.post_data.device_data.model)
                    jsonObjectDeviceData.addProperty("product", modelDevice.post_data.device_data.product)
                    jsonObjectDeviceData.addProperty("manufacturer", modelDevice.post_data.device_data.manufacturer)
                    jsonObjectDeviceData.addProperty("app_version", BuildConfig.VERSION_NAME)
                    jsonObjectDeviceData.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

                    jsonObjectDeviceInfo.add("device_data", jsonObjectDeviceData)

                    val jsonObject = JsonObject()

                    jsonObject.addProperty("phone_number", modelDevice.access_phone)
                    jsonObject.addProperty("apps_pin", pass_code_view.passCodeText.toString())
                    jsonObject.add("device_info", jsonObjectDeviceInfo)

                    pass_code_view.reset()

                   // println("JSON"+jsonObject)

                    val presenterPinSet = PresenterPinSet()
                    presenterPinSet.dosetPin(this@PinSetActivity, jsonObject, this)


                } else
                {
                    CommonMethod.customSnackBarError(rootLayout, this@PinSetActivity, resources.getString(R.string.no_internet))
                    pass_code_view.setError(true)
                }
            }
        })

    }



    override fun onSuccessPinSet(data: LoginData) {

        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        sharedPrefOBJ.accessToken   =data.access_token
        val json =  Gson().toJson(data)
        sharedPrefOBJ.userInfo      = json
        sharedPrefOBJ.userCreditScore=data.user_info.credit_score.toString()

        val presenterDashboard= PresenterDashboard()
        presenterDashboard.getDashboard(this@PinSetActivity,data.access_token,this)

    }

    override fun onErrorPinSet(message: String) {

        dismiss()
        pass_code_view.setError(true)
        Toast.makeText(this@PinSetActivity,message,Toast.LENGTH_SHORT).show()
    }

    override fun onRequiredEmail(data: LoginData) {

        dismiss()
        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        sharedPrefOBJ.accessToken   =data.access_token
        val json =  Gson().toJson(data)
        sharedPrefOBJ.userInfo      = json
        sharedPrefOBJ.userCreditScore=data.user_info.credit_score.toString()
        startActivity(Intent(this@PinSetActivity, EmailRequiredActivity::class.java))
        overridePendingTransition(R.anim.right_in,R.anim.left_out)
        finish()

    }
    override fun onVerifyEmail(data: LoginData) {

        dismiss()
        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        sharedPrefOBJ.accessToken   =data.access_token
        val json =  Gson().toJson(data)
        sharedPrefOBJ.userInfo      = json
        sharedPrefOBJ.userCreditScore=data.user_info.credit_score.toString()
        startActivity(Intent(this@PinSetActivity, EmailVerificationActivity::class.java))
        overridePendingTransition(R.anim.right_in,R.anim.left_out)
        finish()

    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {

        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        val dashBoardData          = Gson().toJson(data)
        sharedPrefOBJ.userDashBoard       = dashBoardData

        val presenterHelp= PresenterHelp()
        presenterHelp.getHelp(this@PinSetActivity,this)

    }

    override fun onFailureDashboard(jsonMessage: String) {

    dismiss()
    CommonMethod.customSnackBarError(rootLayout,this@PinSetActivity,jsonMessage)

    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@PinSetActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                val sharedPrefOBJ= SharedPref(this@PinSetActivity)
                sharedPrefOBJ.removeShared()
                startActivity(Intent(this@PinSetActivity, MainActivity::class.java))
                overridePendingTransition(R.anim.right_in,R.anim.left_out)
                finish()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }

    override fun onSuccessHelp(data: HelpData) {

        val sharedPrefOBJ=SharedPref(this@PinSetActivity)
        val helpSupportData      = Gson().toJson(data)
        sharedPrefOBJ.helpSupport       = helpSupportData
        dismiss()
        val intent = Intent(this@PinSetActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()

    }

    override fun onErrorHelp(message: String) {
        dismiss()
        pass_code_view.setError(true)
        Toast.makeText(this@PinSetActivity,message,Toast.LENGTH_SHORT).show()
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount)
        {
            val view = toolbar.getChildAt(i)
            if (view is TextView)
            {
                val titleFont =   Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text ==    toolbar.title) {
                    view.typeface = titleFont
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
            true
        }

       else -> super.onOptionsItemSelected(item)
       }
   }
 }

