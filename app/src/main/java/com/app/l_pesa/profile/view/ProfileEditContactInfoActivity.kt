package com.app.l_pesa.profile.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.login.inter.ICallBackLogin
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.login.presenter.PresenterLogin
import com.app.l_pesa.profile.inter.ICallBackContactInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterContactInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_profile_edit_contact_info.*
import kotlinx.android.synthetic.main.content_profile_edit_contact_info.*

class ProfileEditContactInfoActivity : AppCompatActivity(), ICallBackContactInfo, ICallBackLogin {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_contact_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditContactInfoActivity)

        initData()
        initClickEvent()

    }

    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditContactInfoActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

        if(profileData!=null)
        {
            etAddress.setText(profileData.userContactInfo.streetAddress)
            etPostalAddress.setText(profileData.userContactInfo.postalAddress)
            etCity.setText(profileData.userContactInfo.city)
            etEmail.setText(profileData.userPersonalInfo.emailAddress)
            etMob.setText(profileData.userContactInfo.phoneNumber)

        }
    }

    private fun initClickEvent()
    {

        buttonSubmit.setOnClickListener {

            if(TextUtils.isEmpty(etAddress.text.toString()))
            {
                customSnackBarError(llRoot,resources.getString(R.string.required_street_address))
            }
            else if(TextUtils.isEmpty(etPostalAddress.text.toString()))
            {
                customSnackBarError(llRoot,resources.getString(R.string.required_postal_code))
            }
            else if(TextUtils.isEmpty(etCity.text.toString()))
            {
                customSnackBarError(llRoot,resources.getString(R.string.required_city))
            }
            else if(TextUtils.isEmpty(etEmail.text.toString()))
            {
                customSnackBarError(llRoot,resources.getString(R.string.required_email))
            }
            else if(!TextUtils.isEmpty(etMob.text.toString()) && etMob.text.toString().length<9)
            {
                customSnackBarError(llRoot,resources.getString(R.string.required_phone))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(this@ProfileEditContactInfoActivity))
                {
                    buttonSubmit.isClickable=false
                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                    swipeRefreshLayout.isRefreshing=true

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("street_address",etAddress.text.toString())
                    jsonObject.addProperty("postal_address",etPostalAddress.text.toString())
                    jsonObject.addProperty("city",etCity.text.toString())
                    jsonObject.addProperty("email",etEmail.text.toString())
                    jsonObject.addProperty("phone_number",etMob.text.toString())

                    val presenterContactInfo= PresenterContactInfo()
                    presenterContactInfo.doChangeContactInfo(this@ProfileEditContactInfoActivity,jsonObject,this)
                }
                else
                {

                    customSnackBarError(llRoot,resources.getString(R.string.no_internet))
                }
            }
        }

        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    override fun onSuccessContactInfo() {

        val sharedPrefOBJ = SharedPref(this@ProfileEditContactInfoActivity)
        val jsonObject = JsonParser().parse(sharedPrefOBJ.loginRequest).asJsonObject
        val presenterLoginObj = PresenterLogin()
        presenterLoginObj.doLogin(this@ProfileEditContactInfoActivity, jsonObject, this)
    }

    override fun onFailureContactInfo(message: String) {

        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        customSnackBarError(llRoot,message)
    }

    override fun onSuccessLogin(data: LoginData) {

        val sharedPrefOBJ=SharedPref(this@ProfileEditContactInfoActivity)
        sharedPrefOBJ.accessToken   =data.access_token
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPrefOBJ.userInfo      = json

        swipeRefreshLayout.isRefreshing=false
        val intent = Intent(this@ProfileEditContactInfoActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorLogin(jsonMessage: String) {
        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        Toast.makeText(this@ProfileEditContactInfoActivity,jsonMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onFailureLogin(jsonMessage: String) {
        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(llRoot,this@ProfileEditContactInfoActivity,jsonMessage)
    }

    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ProfileEditContactInfoActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this@ProfileEditContactInfoActivity).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message

        snackBarOBJ.show()
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

                if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditContactInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }
              true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

}
