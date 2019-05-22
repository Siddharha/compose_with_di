package com.app.l_pesa.pinview.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.model.PinData
import com.app.l_pesa.login.view.LoginActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.content_pin_set.*



class PinSetActivity : AppCompatActivity() {

    private lateinit  var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_set)

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

    private fun initData()
    {
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        pass_code_view.setTypeFace(face)

        val sharedPrefOBJ= SharedPref(this@PinSetActivity)
        val gsonData = Gson().fromJson<PinData>(sharedPrefOBJ.deviceInfo, PinData::class.java)


        pass_code_view.setOnTextChangeListener { text ->
            if (text.length == 6) {

                if(CommonMethod.isNetworkAvailable(this@PinSetActivity))
                {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("phone_number",gsonData.access_phone)
                    jsonObject.addProperty("apps_pin",pass_code_view.passCodeText)

                    val jsonObjectRequestChild = JsonObject()
                    jsonObjectRequestChild.addProperty("phone_no", gsonData.post_data.phone_no)
                    jsonObjectRequestChild.addProperty("country_code", gsonData.post_data.country_code)
                    jsonObjectRequestChild.addProperty("platform_type", gsonData.post_data.platform_type)
                    jsonObjectRequestChild.addProperty("device_token", gsonData.post_data.device_token)

                    val jsonObjectRequestChildDevice = JsonObject()
                    jsonObjectRequestChildDevice.addProperty("device_id", gsonData.post_data.device_data.device_id)
                    jsonObjectRequestChildDevice.addProperty("sdk",gsonData.post_data.device_data.sdk)
                    jsonObjectRequestChildDevice.addProperty("imei",gsonData.post_data.device_data.imei)
                    jsonObjectRequestChildDevice.addProperty("imsi",gsonData.post_data.device_data.imsi)
                    jsonObjectRequestChildDevice.addProperty("simSerial_no",gsonData.post_data.device_data.simSerial_no)
                    jsonObjectRequestChildDevice.addProperty("sim_operator_Name",gsonData.post_data.device_data.sim_operator_Name)
                    jsonObjectRequestChildDevice.addProperty("screen_height",gsonData.post_data.device_data.screen_height)
                    jsonObjectRequestChildDevice.addProperty("screen_width",gsonData.post_data.device_data.screen_width)
                    jsonObjectRequestChildDevice.addProperty("device", gsonData.post_data.device_data.device)
                    jsonObjectRequestChildDevice.addProperty("model", gsonData.post_data.device_data.model)
                    jsonObjectRequestChildDevice.addProperty("product", gsonData.post_data.device_data.product)
                    jsonObjectRequestChildDevice.addProperty("manufacturer", gsonData.post_data.device_data.manufacturer)

                    jsonObjectRequestChild.add("post_data",jsonObjectRequestChildDevice)
                    jsonObjectRequestChild.add("device_info",jsonObjectRequestChildDevice)


                    println("REQUEST"+jsonObject.toString())

                   // progressDialog.show()
                   // val presenterPinSet= PresenterPinSet()
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@PinSetActivity,resources.getString(R.string.no_internet))
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
