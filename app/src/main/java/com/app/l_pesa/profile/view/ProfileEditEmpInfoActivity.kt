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
import com.app.l_pesa.profile.inter.ICallBackEmpInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterEmpInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_profile_edit_emp_info.*
import kotlinx.android.synthetic.main.content_profile_edit_emp_info.*

class ProfileEditEmpInfoActivity : AppCompatActivity(), ICallBackEmpInfo, ICallBackLogin {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_emp_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditEmpInfoActivity)


        val sharedPrefOBJ= SharedPref(this@ProfileEditEmpInfoActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        initData(profileData)
        buttonClickEvent(profileData)
    }

    private fun initData(profileData: ResUserInfo.Data)
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            swipeRefreshLayout.isRefreshing=false
        }

           if(!TextUtils.isEmpty(profileData.userEmploymentInfo.employerType))
            {
                etTypeEmp.setText(profileData.userEmploymentInfo.employerType)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo.employerName))
            {
                etNameEmp.setText(profileData.userEmploymentInfo.employerName)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo.department))
            {
                etDepartment.setText(profileData.userEmploymentInfo.department)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo.position))
            {
                etPosition.setText(profileData.userEmploymentInfo.position)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo.employeesIdNumber))
            {
                etId.setText(profileData.userEmploymentInfo.employeesIdNumber)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo.city))
            {
                etCity.setText(profileData.userEmploymentInfo.city)
            }


    }

    private fun buttonClickEvent(profileData: ResUserInfo.Data)
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        buttonSubmit.setOnClickListener {


            val hashMapOLD = HashMap<String, String>()
            hashMapOLD["type"]          = ""+profileData.userEmploymentInfo.employerType
            hashMapOLD["name"]          = ""+profileData.userEmploymentInfo.employerName
            hashMapOLD["department"]    = ""+profileData.userEmploymentInfo.department
            hashMapOLD["position"]      = ""+profileData.userEmploymentInfo.position
            hashMapOLD["id"]            = ""+profileData.userEmploymentInfo.employeesIdNumber
            hashMapOLD["city"]          = ""+profileData.userEmploymentInfo.city

            val hashMapNew = HashMap<String, String>()
            hashMapNew["type"]          = etTypeEmp.text.toString()
            hashMapNew["name"]          = etNameEmp.text.toString()
            hashMapNew["department"]    = etDepartment.text.toString()
            hashMapNew["position"]      = etPosition.text.toString()
            hashMapNew["id"]            = etId.text.toString()
            hashMapNew["city"]          = etCity.text.toString()

            if(hashMapOLD == hashMapNew)
            {
                CommonMethod.customSnackBarError(llRoot,this@ProfileEditEmpInfoActivity,resources.getString(R.string.change_one_info))
            }
            else
            {
                if(TextUtils.isEmpty(etTypeEmp.text.toString()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_type))
                }
                else if(TextUtils.isEmpty(etNameEmp.text.toString()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_name))
                }
                else if(TextUtils.isEmpty(etDepartment.text.toString()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_department))
                }
                else if(TextUtils.isEmpty(etPosition.text.toString()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_occupation))
                }
                else if(TextUtils.isEmpty(etId.text.toString()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_id))
                }
                else if(TextUtils.isEmpty(etCity.text.toString()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_city))
                }
                else
                {
                    if(CommonMethod.isNetworkAvailable(this@ProfileEditEmpInfoActivity))
                    {
                        buttonSubmit.isClickable=false
                        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                        swipeRefreshLayout.isRefreshing=true

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("employer_type",etTypeEmp.text.toString())
                        jsonObject.addProperty("employer_name",etNameEmp.text.toString())
                        jsonObject.addProperty("department",etDepartment.text.toString())
                        jsonObject.addProperty("position",etPosition.text.toString())
                        jsonObject.addProperty("employees_id_number",etId.text.toString())
                        jsonObject.addProperty("city",etCity.text.toString())

                        val presenterEmpInfo= PresenterEmpInfo()
                        presenterEmpInfo.doChangeEmpInfo(this@ProfileEditEmpInfoActivity,jsonObject,this)

                    }
                    else
                    {
                        customSnackBarError(llRoot,resources.getString(R.string.no_internet))
                    }
                }

            }



        }
    }

    override fun onSuccessEmpInfo() {

        val sharedPrefOBJ = SharedPref(this@ProfileEditEmpInfoActivity)
        val jsonObject = JsonParser().parse(sharedPrefOBJ.loginRequest).asJsonObject
        val presenterLoginObj = PresenterLogin()
        presenterLoginObj.doLogin(this@ProfileEditEmpInfoActivity, jsonObject, this)
    }

    override fun onFailureEmpInfo(message: String) {

        buttonSubmit.isClickable=true
        swipeRefreshLayout.isRefreshing=false
    }

    override fun onSuccessLogin(data: LoginData) {

        val sharedPrefOBJ=SharedPref(this@ProfileEditEmpInfoActivity)
        sharedPrefOBJ.accessToken   =data.access_token
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPrefOBJ.userInfo      = json

        swipeRefreshLayout.isRefreshing=false
        val intent = Intent(this@ProfileEditEmpInfoActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorLogin(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        Toast.makeText(this@ProfileEditEmpInfoActivity,jsonMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onFailureLogin(jsonMessage: String) {
        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(llRoot,this@ProfileEditEmpInfoActivity,jsonMessage)
    }

    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ProfileEditEmpInfoActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this@ProfileEditEmpInfoActivity).inflate(R.layout.snackbar_error, null)
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
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditEmpInfoActivity,resources.getString(R.string.please_wait))
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
