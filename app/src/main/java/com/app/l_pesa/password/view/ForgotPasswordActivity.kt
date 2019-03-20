package com.app.l_pesa.password.view

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
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

class ForgotPasswordActivity : AppCompatActivity(), ICallBackPassword, ICallBackCountryList {

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
        CommonMethod.hideKeyboard(this@ForgotPasswordActivity)
        if(etPhone.text.toString().length<9 && !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
        {
            customSnackBarError(ll_root,resources.getString(R.string.required_phone_email))
        }
        else if(!TextUtils.isEmpty(etPhone.text.toString()) && !TextUtils.isEmpty(etEmail.text.toString()))
        {
            customSnackBarError(ll_root,resources.getString(R.string.required_phone_email))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@ForgotPasswordActivity))
            {
                progressBar.visibility= View.VISIBLE
                val jsonObject = JsonObject()
                if(!TextUtils.isEmpty(etPhone.text.toString()) && !TextUtils.isEmpty(etEmail.text.toString()))
                {
                    jsonObject.addProperty("phone_no",etPhone.text.toString())
                }
                else if(TextUtils.isEmpty(etPhone.text.toString()) && !TextUtils.isEmpty(etEmail.text.toString()))
                {
                    jsonObject.addProperty("phone_no",etEmail.text.toString())
                }
                else
                {
                    jsonObject.addProperty("phone_no",etPhone.text.toString())
                }

                jsonObject.addProperty("country_code",countryCode)

                val presenterForgetPassword=PresenterPassword()
                presenterForgetPassword.doForgetPassword(this@ForgotPasswordActivity,jsonObject,this)
            }
            else
            {
                customSnackBarError(ll_root,resources.getString(R.string.no_internet))
            }
        }

    }

    private fun customSnackBarError(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ForgotPasswordActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }


    override fun onSuccessResetPassword(message: String) {

        progressBar.visibility= View.INVISIBLE

    }

    override fun onErrorResetPassword(jsonMessage: String) {

        progressBar.visibility= View.INVISIBLE
        customSnackBarError(ll_root,jsonMessage)
    }
    private fun back()
    {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onBackPressed() {

        back()
    }

    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@ForgotPasswordActivity)
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
                    Glide.with(this@ForgotPasswordActivity)
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
                Glide.with(this@ForgotPasswordActivity)
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
        val dialog= Dialog(this@ForgotPasswordActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView    = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val countryAdapter              = CountryListAdapter(this@ForgotPasswordActivity, countryList.countries_list,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ForgotPasswordActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = countryAdapter
        dialog.show()
    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

        val sharedPref          =SharedPref(this@ForgotPasswordActivity)
        sharedPref.countryCode  =resModelCountryList.code
        countryCode             =resModelCountryList.country_code
        val options = RequestOptions()
        Glide.with(this@ForgotPasswordActivity)
                .load(resModelCountryList.image)
                .apply(options)
                .into(img_country)


    }
}
