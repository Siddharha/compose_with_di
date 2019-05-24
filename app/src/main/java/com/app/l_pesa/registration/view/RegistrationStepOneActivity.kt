package com.app.l_pesa.registration.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Toast

import com.app.l_pesa.R
import com.app.l_pesa.common.CommonEditTextRegular
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.registration.inter.ICallBackRegisterOne
import com.app.l_pesa.registration.model.RegistrationData
import com.app.l_pesa.registration.presenter.PresenterRegistrationOne
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD

import kotlinx.android.synthetic.main.activity_registration_step_one.*


class RegistrationStepOneActivity : AppCompatActivity(), ICallBackCountryList,ICallBackRegisterOne {

    private lateinit  var progressDialog: KProgressHUD
    private var countryCode     ="+255"
    private var countryFound    = false

    private var listCountry                 : ArrayList<ResModelCountryList>? = null
    private lateinit var adapterCountry     : CountryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_one)

        initLoader()
        loadCountry()
        checkQualify()
    }


    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@RegistrationStepOneActivity)
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

    private fun checkQualify()
    {
        etEmail.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        txtQualify.setOnClickListener {

            verifyField()
        }
    }

    @SuppressLint("MissingPermission")
    private fun verifyField()
    {
        CommonMethod.hideKeyboardView(this@RegistrationStepOneActivity)
        if((etPhone.text.toString().length<9))
        {
            CommonMethod.customSnackBarError(ll_root,this@RegistrationStepOneActivity,resources.getString(R.string.required_phone))
        }
        else if(TextUtils.isEmpty(etEmail.text.toString()) || !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(ll_root,this@RegistrationStepOneActivity,resources.getString(R.string.required_email))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@RegistrationStepOneActivity))
            {
                progressDialog.show()
                txtQualify.isClickable=false

                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val telephonyManager    = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

                var imeiId=""
                imeiId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager!!.imei
                } else {
                    telephonyManager!!.deviceId
                }

                val deviceId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("email_address",etEmail.text.toString())
                jsonObject.addProperty("country_code",countryCode)
                jsonObject.addProperty("platform_type","A")
                jsonObject.addProperty("device_token", FirebaseInstanceId.getInstance().token.toString())

                val jsonObjectRequestChild = JsonObject()
                jsonObjectRequestChild.addProperty("device_id", deviceId)
                jsonObjectRequestChild.addProperty("sdk",""+Build.VERSION.SDK_INT)
                jsonObjectRequestChild.addProperty("imei",imeiId)
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

                println("JSON"+jsonObject)

                val presenterRegistrationOneObj= PresenterRegistrationOne()
                presenterRegistrationOneObj.doRegistration(this@RegistrationStepOneActivity,jsonObject,this)
            }
            else
            {
                CommonMethod.customSnackBarError(ll_root,this@RegistrationStepOneActivity,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@RegistrationStepOneActivity)
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
                    options.centerCrop()
                    Glide.with(this@RegistrationStepOneActivity)
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
                options.centerCrop()
                Glide.with(this@RegistrationStepOneActivity)
                        .load(countryData.countries_list[0].image)
                        .apply(options)
                        .into(img_country)
                countryCode="+255"
            }
        }


        ll_flag_section.setOnClickListener {
            val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)
            countrySpinner(countryData)
        }
    }

    private fun countrySpinner(countryList: ResModelData)
    {
        val dialog= Dialog(this@RegistrationStepOneActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        listCountry= ArrayList()
        val recyclerView    = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        listCountry!!.addAll(countryList.countries_list)
        adapterCountry                  = CountryListAdapter(this@RegistrationStepOneActivity, listCountry!!,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepOneActivity, LinearLayoutManager.VERTICAL, false)
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

        val options = RequestOptions()
        options.centerCrop()
        Glide.with(this@RegistrationStepOneActivity)
                .load(resModelCountryList.image)
                .apply(options)
                .into(img_country)
        countryCode=resModelCountryList.country_code
    }



    override fun onBackPressed() {

        val intent = Intent(this@RegistrationStepOneActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onSuccessRegistrationOne(data: RegistrationData) {

        dismiss()
        Toast.makeText(this@RegistrationStepOneActivity,resources.getString(R.string.refer_to_otp), Toast.LENGTH_LONG).show()
        txtQualify.isClickable =true
        val sharedPref = SharedPref(this@RegistrationStepOneActivity)
        sharedPref.accessToken=data.access_token

        val bundle = Bundle()
        bundle.putString("OTP",data.otp)
        val intent = Intent(this@RegistrationStepOneActivity, RegistrationStepTwoActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorRegistrationOne(jsonMessage: String) {

        dismiss()
        txtQualify.isClickable=true
        CommonMethod.customSnackBarError(ll_root,this@RegistrationStepOneActivity,jsonMessage)
    }
}
