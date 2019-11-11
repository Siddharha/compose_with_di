package com.app.l_pesa.otpview.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypefaceSpan
import com.app.l_pesa.common.OnOtpCompletionListener
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.otpview.inter.ICallBackVerifyOTP
import com.app.l_pesa.otpview.model.PinData
import com.app.l_pesa.otpview.presenter.PresenterOTP
import com.app.l_pesa.pinview.view.PinSetActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.content_otp.*
import java.util.concurrent.TimeUnit


class OTPActivity : AppCompatActivity(), OnOtpCompletionListener, ICallBackVerifyOTP {


    private var clickCount=0
    private lateinit var  progressDialog   : ProgressDialog

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

    private fun loadResend() {

        txtResend.visibility = View.VISIBLE
        if(clickCount>1)
        {
            txtResend.visibility = View.INVISIBLE
        }
        else
        {
            txtResend.visibility = View.VISIBLE
        }

            txtResend.setOnClickListener {

                if(CommonMethod.isNetworkAvailable(this@OTPActivity))
                {
                    val logger = AppEventsLogger.newLogger(this@OTPActivity)
                    val params =  Bundle()
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "OTP Resend")
                    logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

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
                    jsonObjectDeviceData.addProperty("app_version", BuildConfig.VERSION_NAME)
                    jsonObjectDeviceData.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

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
        clickCount++
        progressDialog.dismiss()
        if(clickCount>3)
        {
            txtResend.visibility = View.INVISIBLE
        }
        else
        {
            loadTimer()
        }

    }

    override fun onErrorResendOTP(message: String) {

        progressDialog.dismiss()
        Toast.makeText(this@OTPActivity,message,Toast.LENGTH_SHORT).show()
    }

    private fun loadTimer()
    {
        txtResend.visibility= View.INVISIBLE
        txtTimer.visibility= View.VISIBLE
        val noOfMinutes = 60 * 3000
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

            val logger = AppEventsLogger.newLogger(this@OTPActivity)
            val params =  Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "OTP Set")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)


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
            jsonObjectDeviceData.addProperty("app_version", BuildConfig.VERSION_NAME)
            jsonObjectDeviceData.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

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
        progressDialog = ProgressDialog(this@OTPActivity,R.style.MyAlertDialogStyle)
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
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@OTPActivity::class.java.simpleName)

    }

}
