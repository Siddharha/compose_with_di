package com.app.l_pesa.profile.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.profile.inter.ICallBackContactInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterContactInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_profile_edit_contact_info.*
import kotlinx.android.synthetic.main.content_profile_edit_contact_info.*

class ProfileEditContactInfoActivity : AppCompatActivity(), ICallBackContactInfo {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_contact_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditContactInfoActivity)

        val sharedPrefOBJ= SharedPref(this@ProfileEditContactInfoActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        initData(profileData)
        initClickEvent(profileData)

    }

    private fun initData(profileData: ResUserInfo.Data)
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            swipeRefreshLayout.isRefreshing=false
        }

           if(!TextUtils.isEmpty(profileData.userContactInfo!!.streetAddress))
            {
                etAddress.setText(profileData.userContactInfo!!.streetAddress)
            }
            if(!TextUtils.isEmpty(profileData.userContactInfo!!.postalAddress))
            {
                etPostalAddress.setText(profileData.userContactInfo!!.postalAddress)
            }
            if(!TextUtils.isEmpty(profileData.userContactInfo!!.city))
            {
                etCity.setText(profileData.userContactInfo!!.city)
            }
            if(!TextUtils.isEmpty(profileData.userContactInfo!!.phoneNumber))
            {
                etMob.setText(profileData.userContactInfo!!.phoneNumber)
            }


    }

    private fun initClickEvent(profileData: ResUserInfo.Data)
    {

        buttonSubmit.setOnClickListener {

            val hashMapOLD = HashMap<String, String>()
            hashMapOLD["address"]           = ""+profileData.userContactInfo!!.streetAddress
            hashMapOLD["postal"]            = ""+profileData.userContactInfo!!.postalAddress
            hashMapOLD["city"]              = ""+profileData.userContactInfo!!.city
            hashMapOLD["mob"]               = ""+profileData.userContactInfo!!.phoneNumber

            val hashMapNew = HashMap<String, String>()
            hashMapNew["address"]          = etAddress.text.toString()
            hashMapNew["postal"]           = etPostalAddress.text.toString()
            hashMapNew["city"]             = etCity.text.toString()
            hashMapNew["mob"]              = etMob.text.toString()


            CommonMethod.hideKeyboardView(this@ProfileEditContactInfoActivity)
            if(hashMapOLD == hashMapNew)
            {
                CommonMethod.customSnackBarError(llRoot,this@ProfileEditContactInfoActivity,resources.getString(R.string.change_one_info))
            }
            else
            {
                if(TextUtils.isEmpty(etAddress.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_street_address))
                }
                else if(TextUtils.isEmpty(etPostalAddress.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_postal_code))
                }
                else if(TextUtils.isEmpty(etCity.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_city))
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

        }

        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    override fun onSuccessContactInfo() {

        swipeRefreshLayout.isRefreshing=false
        val sharedPrefOBJ = SharedPref(this@ProfileEditContactInfoActivity)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)
        onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onFailureContactInfo(message: String) {

        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        customSnackBarError(llRoot,message)
    }

    override fun onSessionTimeOut(message: String) {
        swipeRefreshLayout.isRefreshing=false

        val dialogBuilder = AlertDialog.Builder(this@ProfileEditContactInfoActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@ProfileEditContactInfoActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@ProfileEditContactInfoActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

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

                if(swipeRefreshLayout.isRefreshing && CommonMethod.isNetworkAvailable(this@ProfileEditContactInfoActivity))
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditContactInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    CommonMethod.hideKeyboardView(this@ProfileEditContactInfoActivity)
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

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@ProfileEditContactInfoActivity::class.java.simpleName)

    }

}
