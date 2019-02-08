package com.app.l_pesa.password.view

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
import com.app.l_pesa.password.inter.ICallBackPassword
import com.app.l_pesa.password.presenter.PresenterPassword
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : AppCompatActivity(), ICallBackPassword, ICallBackCountryList {

    private var countryCode     ="+255"
    private var countryFound    = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        loadCountry()
        forgetPassword()
    }


    private fun forgetPassword()
    {

        etEmail.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        txtSubmit.setOnClickListener {
            verifyField()

        }

        txtCancel.setOnClickListener {
            back()

        }
    }

    private fun verifyField()
    {
        CommonMethod.hideKeyboard(this@ForgetPasswordActivity)
        if(etPhone.text.toString().length<10 && !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(ll_root,resources.getString(R.string.required_phone_email),this@ForgetPasswordActivity)
        }
        else if(!TextUtils.isEmpty(etPhone.text.toString()) && !TextUtils.isEmpty(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(ll_root,resources.getString(R.string.required_phone_email),this@ForgetPasswordActivity)
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@ForgetPasswordActivity))
            {
                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("country_code",countryCode)

                val presenterForgetPassword=PresenterPassword()
                presenterForgetPassword.doForgetPassword(this@ForgetPasswordActivity,jsonObject,this)
            }
            else
            {
                CommonMethod.customSnackBarError(ll_root,resources.getString(R.string.no_internet),this@ForgetPasswordActivity)
            }
        }

    }

    override fun onSuccessResetPassword(message: String) {

        CommonMethod.customSnackBarSuccess(ll_root,message,this@ForgetPasswordActivity)
    }

    override fun onErrorResetPassword(jsonMessage: String) {

        CommonMethod.customSnackBarError(ll_root,jsonMessage,this@ForgetPasswordActivity)
    }
    private fun back()
    {
        val intent = Intent(this@ForgetPasswordActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onBackPressed() {

        back()
    }

    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@ForgetPasswordActivity)
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
                    Glide.with(this@ForgetPasswordActivity)
                            .load(countryListCode.image)
                            .apply(options)
                            .into(img_country)
                    countryCode=countryListCode.country_code

                }

            }
            if(!countryFound)
            {
                val options = RequestOptions()
                options.centerCrop()
                Glide.with(this@ForgetPasswordActivity)
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
        val dialog= Dialog(this@ForgetPasswordActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView    = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val countryAdapter              = CountryListAdapter(this@ForgetPasswordActivity, countryList.countries_list,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ForgetPasswordActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = countryAdapter
        dialog.show()
    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

        val sharedPref          =SharedPref(this@ForgetPasswordActivity)
        sharedPref.countryCode  =resModelCountryList.code
        countryCode             =resModelCountryList.country_code
        val options = RequestOptions()
        options.centerCrop()
        Glide.with(this@ForgetPasswordActivity)
                .load(resModelCountryList.image)
                .apply(options)
                .into(img_country)


    }
}
