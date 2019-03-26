package com.app.l_pesa.registration.view

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Toast

import com.app.l_pesa.R
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
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_registration_step_one.*


class RegistrationStepOneActivity : AppCompatActivity(), ICallBackCountryList,ICallBackRegisterOne {


    private var countryCode     ="+255"
    private var countryFound    = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_one)

        loadCountry()
        checkQualify()
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
                swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                swipeRefreshLayout.isRefreshing=true
                txtQualify.isClickable=false

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("country_code",countryCode)
                jsonObject.addProperty("email_address",etEmail.text.toString())
                jsonObject.addProperty("device_token"," ")
                jsonObject.addProperty("platform_type","A")

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
        val recyclerView    = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val countryAdapter              = CountryListAdapter(this@RegistrationStepOneActivity, countryList.countries_list,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepOneActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = countryAdapter
        dialog.show()
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

        Toast.makeText(this@RegistrationStepOneActivity,resources.getString(R.string.refer_to_otp), Toast.LENGTH_LONG).show()
        swipeRefreshLayout.isRefreshing=false
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

        swipeRefreshLayout.isRefreshing=false
        txtQualify.isClickable=true
        CommonMethod.customSnackBarError(ll_root,this@RegistrationStepOneActivity,jsonMessage)
    }
}
