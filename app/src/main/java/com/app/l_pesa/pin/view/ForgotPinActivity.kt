package com.app.l_pesa.pin.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonEditTextRegular
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.otpview.view.OTPActivity
import com.app.l_pesa.pin.inter.ICallBackChangePin
import com.app.l_pesa.pin.model.PinData
import com.app.l_pesa.pin.presenter.PresenterPassword
import com.app.l_pesa.pinview.view.PinSetActivity
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_forgot_pin.*

class ForgotPinActivity : AppCompatActivity(),  ICallBackCountryList, ICallBackChangePin {

    private var countryCode     ="+255"
    private var countryFound    = false

    private var listCountry                 : ArrayList<ResModelCountryList>? = null
    private lateinit var adapterCountry     : CountryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pin)

        loadCountry()
        forgetPin()
    }

    private fun forgetPin()
    {

        buttonSubmit.setOnClickListener {
            verifyField()

        }

    }

    @SuppressLint("MissingPermission")
    private fun verifyField()
    {
        val telephonyManager    = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        var getIMEI=""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId
        }

        val deviceId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        CommonMethod.hideKeyboardView(this@ForgotPinActivity)

        if(etPhone.text.toString().length<9 )
        {
            customSnackBarError(ll_root,resources.getString(R.string.required_phone_email))
        }
        else if(TextUtils.isEmpty(telephonyManager.simSerialNumber))
        {
            CommonMethod.customSnackBarError(ll_root,this@ForgotPinActivity,resources.getString(R.string.required_sim))
        }

        else
        {

            if(CommonMethod.isNetworkAvailable(this@ForgotPinActivity))
            {
                buttonSubmit.isClickable =false
                progressBar.visibility= View.VISIBLE

                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("country_code",countryCode)
                jsonObject.addProperty("platform_type","A")
                jsonObject.addProperty("device_token", FirebaseInstanceId.getInstance().token.toString())

                val jsonObjectRequestChild = JsonObject()
                jsonObjectRequestChild.addProperty("device_id", deviceId)
                jsonObjectRequestChild.addProperty("sdk",""+ Build.VERSION.SDK_INT)
                jsonObjectRequestChild.addProperty("imei",getIMEI)
                jsonObjectRequestChild.addProperty("imsi",""+telephonyManager.subscriberId)
                jsonObjectRequestChild.addProperty("simSerial_no",""+telephonyManager.simSerialNumber)
                jsonObjectRequestChild.addProperty("sim_operator_Name",telephonyManager.simOperatorName)
                jsonObjectRequestChild.addProperty("screen_height",""+height)
                jsonObjectRequestChild.addProperty("screen_width",""+width)
                jsonObjectRequestChild.addProperty("device", Build.DEVICE)
                jsonObjectRequestChild.addProperty("model", Build.MODEL)
                jsonObjectRequestChild.addProperty("product", Build.PRODUCT)
                jsonObjectRequestChild.addProperty("manufacturer", Build.MANUFACTURER)

                jsonObject.add("device_data",jsonObjectRequestChild)

                val presenterForgetPassword= PresenterPassword()
                presenterForgetPassword.doForgetPassword(this@ForgotPinActivity,jsonObject,this)
            }
            else
            {
                customSnackBarError(ll_root,resources.getString(R.string.no_internet))
            }
        }

    }

    private fun customSnackBarError(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ForgotPinActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message

        snackBarOBJ.show()
    }

    override fun onSuccessResetPin(data: PinData) {
        progressBar.visibility= View.INVISIBLE
        if(data.next_step=="next_otp")
        {
            Toast.makeText(this@ForgotPinActivity,resources.getString(R.string.sent_otp_via_sms),Toast.LENGTH_LONG).show()
            val sharedPrefOBJ=SharedPref(this@ForgotPinActivity)
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@ForgotPinActivity, OTPActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        else
        {
            Toast.makeText(this@ForgotPinActivity,resources.getString(R.string.sent_pin_via_sms),Toast.LENGTH_LONG).show()
            val sharedPrefOBJ=SharedPref(this@ForgotPinActivity)
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@ForgotPinActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onErrorResetPin(message: String) {

        progressBar.visibility= View.INVISIBLE
        buttonSubmit.isClickable=true
        customSnackBarError(ll_root,message)
    }



    private fun back()
    {
        val intent = Intent(this@ForgotPinActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onBackPressed() {

        back()
    }

    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@ForgotPinActivity)
        val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)

        if(countryData.countries_list.size>0)
        {
            val totalSize = 0 until countryData.countries_list.size

            for(i in totalSize)
            {
                val countryListCode=countryData.countries_list[i]
                if(countryListCode.code==sharedPrefOBJ.countryCode)
                {
                    countryFound=true
                    val options = RequestOptions()
                    Glide.with(this@ForgotPinActivity)
                            .load(countryListCode.image)
                            .apply(options)
                            .into(img_country)
                    countryCode=countryListCode.country_code
                    break

                }

            }
            if(!countryFound)
            {
                val options = RequestOptions()
                Glide.with(this@ForgotPinActivity)
                        .load(countryData.countries_list[0].image)
                        .apply(options)
                        .into(img_country)
                countryCode="+255"
            }
        }


        ll_flag_section.setOnClickListener {
            val modelCountry = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)
            countrySpinner(modelCountry)
        }
    }

    private fun countrySpinner(countryList: ResModelData)
    {
        val dialog= Dialog(this@ForgotPinActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        listCountry= ArrayList()
        val recyclerView    = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        listCountry!!.addAll(countryList.countries_list)
        adapterCountry                  = CountryListAdapter(this@ForgotPinActivity, listCountry!!,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ForgotPinActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = adapterCountry
        dialog.show()

        etCountry!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


                filterCountry(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })


    }

    private fun filterCountry(countryName: String) {

        val filteredCourseAry: ArrayList<ResModelCountryList> = ArrayList()

        for (eachCourse in listCountry!!) {
            if (eachCourse.country_name.toLowerCase().startsWith(countryName.toLowerCase())) {
                filteredCourseAry.add(eachCourse)
            }
            else
            {

            }
        }

        if(filteredCourseAry.size==0)
        {
            filteredCourseAry.clear()
            val emptyList=ResModelCountryList(0,resources.getString(R.string.search_result_not_found),"","","","","","","","","","","")
            filteredCourseAry.add(emptyList)
        }

        adapterCountry.filterList(filteredCourseAry)

    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

        val sharedPref          =SharedPref(this@ForgotPinActivity)
        sharedPref.countryCode  =resModelCountryList.code
        countryCode             =resModelCountryList.country_code
        val options = RequestOptions()
        Glide.with(this@ForgotPinActivity)
                .load(resModelCountryList.image)
                .apply(options)
                .into(img_country)


    }
}
