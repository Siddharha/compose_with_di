package com.app.l_pesa.otpview.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.OnOtpCompletionListener
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.model.PinData
import com.app.l_pesa.login.model.PostData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.content_otp.*
import com.google.gson.JsonParser



class OTPActivity : AppCompatActivity(), OnOtpCompletionListener {

    private lateinit  var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@OTPActivity)

        initLoader()
        otp_view.setOtpCompletionListener(this)

     }

    override fun onOtpCompleted(otp: String) {

        if(otp.length>5)
        {
            doVerifyOTP(otp)
        }
    }

    private fun doVerifyOTP(otp: String)
    {
        CommonMethod.hideKeyboardView(this@OTPActivity)

        if(CommonMethod.isNetworkAvailable(this@OTPActivity))
        {
            progressDialog.show()

            val sharedPrefOBJ= SharedPref(this@OTPActivity)
            val modelDevice = Gson().fromJson<PostData>(sharedPrefOBJ.deviceInfo, PostData::class.java)


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
            jsonObject.addProperty("phone_number",modelDevice.phone_no)
            jsonObject.addProperty("otp",otp)
            jsonObject.add("device_info",jsonObjectDeviceInfo)

            println("JSON"+jsonObject)

        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@OTPActivity,resources.getString(R.string.no_internet))
        }

    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@OTPActivity)
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
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
