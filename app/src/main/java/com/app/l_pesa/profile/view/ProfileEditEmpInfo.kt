package com.app.l_pesa.profile.view

import android.app.Activity
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
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_profile_edit_emp_info.*
import kotlinx.android.synthetic.main.content_profile_edit_emp_info.*

class ProfileEditEmpInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_emp_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditEmpInfo)


        initData()
        buttonClickEvent()
    }

    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditEmpInfo)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

        if(profileData!=null)
        {
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
    }

    private fun buttonClickEvent()
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        buttonSubmit.setOnClickListener {


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
                if(CommonMethod.isNetworkAvailable(this@ProfileEditEmpInfo))
                {
                    buttonSubmit.isClickable=false
                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                    swipeRefreshLayout.isRefreshing=true
                }
                else
                {
                    customSnackBarError(llRoot,resources.getString(R.string.no_internet))
                }
            }



        }
    }

    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ProfileEditEmpInfo,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this@ProfileEditEmpInfo).inflate(R.layout.snackbar_error, null)
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

                /*if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditContactInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {*/
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
               // }
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
