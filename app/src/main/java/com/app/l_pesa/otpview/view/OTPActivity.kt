package com.app.l_pesa.otpview.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.OnOtpCompletionListener
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.model.PostData
import com.app.l_pesa.otpview.inter.ICallBackVerifyOTP
import com.app.l_pesa.otpview.model.PinData
import com.app.l_pesa.otpview.presenter.PresenterOTP
import com.app.l_pesa.pinview.view.PinSetActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.content_otp.*
import android.os.CountDownTimer
import android.view.View
import java.util.concurrent.TimeUnit


class OTPActivity : AppCompatActivity(), OnOtpCompletionListener, ICallBackVerifyOTP {


    private lateinit  var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@OTPActivity)

        initLoader()
        loadResend()
        otp_view.setOtpCompletionListener(this)

     }

    override fun onOtpCompleted(otp: String) {

        if(otp.length>5)
        {
            doVerifyOTP(otp)
        }
    }

    private fun loadResend()
    {
        txtResend.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(this@OTPActivity))
            {
                progressDialog.show()
                val sharedPrefOBJ= SharedPref(this@OTPActivity)
                val modelDevice = Gson().fromJson<PinData>(sharedPrefOBJ.deviceInfo, PinData::class.java)

                val jsonObjectDeviceInfo = JsonObject()
                jsonObjectDeviceInfo.addProperty("phone_no", modelDevice.post_data.phone_no)
                jsonObjectDeviceInfo.addProperty("country_code", modelDevice.post_data.country_code)
                jsonObjectDeviceInfo.addProperty("platform_type", modelDevice.post_data.platform_type)
                jsonObjectDeviceInfo.addProperty("device_token", modelDevice.post_data.device_token)

                val jsonObjectDeviceData = JsonObject()
                jsonObjectDeviceData.addProperty("device_id", modelDevice.post_data.device_data.device_id)
                jsonObjectDeviceData.addProperty("sdk",modelDevice.post_data.device_data.sdk)
                jsonObjectDeviceData.addProperty("imei",modelDevice.post_data.device_data.imei)
                jsonObjectDeviceData.addProperty("imsi",modelDevice.post_data.device_data.imsi)
                jsonObjectDeviceData.addProperty("simSerial_no",modelDevice.post_data.device_data.simSerial_no)
                jsonObjectDeviceData.addProperty("sim_operator_Name",modelDevice.post_data.device_data.sim_operator_Name)
                jsonObjectDeviceData.addProperty("screen_height",modelDevice.post_data.device_data.screen_height)
                jsonObjectDeviceData.addProperty("screen_width",modelDevice.post_data.device_data.screen_width)
                jsonObjectDeviceData.addProperty("device", modelDevice.post_data.device_data.device)
                jsonObjectDeviceData.addProperty("model", modelDevice.post_data.device_data.model)
                jsonObjectDeviceData.addProperty("product", modelDevice.post_data.device_data.product)
                jsonObjectDeviceData.addProperty("manufacturer", modelDevice.post_data.device_data.manufacturer)

                jsonObjectDeviceInfo.add("device_data",jsonObjectDeviceData)

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_number",modelDevice.access_phone)
                jsonObject.add("device_info",jsonObjectDeviceInfo)

                val presenterOTP=PresenterOTP()
                presenterOTP.doResendOTP(this@OTPActivity,jsonObject,this)
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,this@OTPActivity,resources.getString(R.string.no_internet))
            }
        }
    }

    override fun onSuccessResendOTP() {

        progressDialog.dismiss()
        loadTimer()
    }

    override fun onErrorResendOTP(message: String) {

        progressDialog.dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@OTPActivity,message)
    }

    private fun loadTimer()
    {
        txtResend.visibility= View.INVISIBLE
        txtTimer.visibility= View.VISIBLE
        val noOfMinutes = 60 * 1000
        object : CountDownTimer(noOfMinutes.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                @SuppressLint("DefaultLocale") val hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                txtTimer.text = hms

            }

            override fun onFinish() {
                txtTimer.visibility= View.INVISIBLE
                txtResend.visibility= View.VISIBLE
            }
        }.start()
    }

    private fun doVerifyOTP(otp: String)
    {
        CommonMethod.hideKeyboardView(this@OTPActivity)

        if(CommonMethod.isNetworkAvailable(this@OTPActivity))
        {
            txtTimer.visibility= View.INVISIBLE
            txtResend.visibility= View.VISIBLE
            progressDialog.show()

            val sharedPrefOBJ= SharedPref(this@OTPActivity)
            val modelDevice = Gson().fromJson<PinData>(sharedPrefOBJ.deviceInfo, PinData::class.java)


            val jsonObjectDeviceInfo = JsonObject()
            jsonObjectDeviceInfo.addProperty("phone_no", modelDevice.post_data.phone_no)
            jsonObjectDeviceInfo.addProperty("country_code", modelDevice.post_data.country_code)
            jsonObjectDeviceInfo.addProperty("platform_type", modelDevice.post_data.platform_type)
            jsonObjectDeviceInfo.addProperty("device_token", modelDevice.post_data.device_token)


            val jsonObjectDeviceData = JsonObject()
            jsonObjectDeviceData.addProperty("device_id", modelDevice.post_data.device_data.device_id)
            jsonObjectDeviceData.addProperty("sdk",modelDevice.post_data.device_data.sdk)
            jsonObjectDeviceData.addProperty("imei",modelDevice.post_data.device_data.imei)
            jsonObjectDeviceData.addProperty("imsi",modelDevice.post_data.device_data.imsi)
            jsonObjectDeviceData.addProperty("simSerial_no",modelDevice.post_data.device_data.simSerial_no)
            jsonObjectDeviceData.addProperty("sim_operator_Name",modelDevice.post_data.device_data.sim_operator_Name)
            jsonObjectDeviceData.addProperty("screen_height",modelDevice.post_data.device_data.screen_height)
            jsonObjectDeviceData.addProperty("screen_width",modelDevice.post_data.device_data.screen_width)
            jsonObjectDeviceData.addProperty("device", modelDevice.post_data.device_data.device)
            jsonObjectDeviceData.addProperty("model", modelDevice.post_data.device_data.model)
            jsonObjectDeviceData.addProperty("product", modelDevice.post_data.device_data.product)
            jsonObjectDeviceData.addProperty("manufacturer", modelDevice.post_data.device_data.manufacturer)

            jsonObjectDeviceInfo.add("device_data",jsonObjectDeviceData)

            val jsonObject = JsonObject()
            jsonObject.addProperty("phone_number",modelDevice.access_phone)
            jsonObject.addProperty("otp",otp)
            jsonObject.add("device_info",jsonObjectDeviceInfo)

            val presenterOTP= PresenterOTP()
            presenterOTP.doVerifyOTP(this@OTPActivity,jsonObject,this)


        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@OTPActivity,resources.getString(R.string.no_internet))
        }

    }

    override fun onSuccessVerifyOTP(data: PinData) {
        dismiss()
        if(data.next_step=="next_pin")
        {
            val sharedPrefOBJ=SharedPref(this@OTPActivity)
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@OTPActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onErrorVerifyOTP(message: String) {
        dismiss()
        otp_view.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,this@OTPActivity,message)
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
