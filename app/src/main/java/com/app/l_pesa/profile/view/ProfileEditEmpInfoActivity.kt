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
import com.app.l_pesa.profile.inter.ICallBackEmpInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterEmpInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_profile_edit_emp_info.*
import kotlinx.android.synthetic.main.content_profile_edit_emp_info.*

class ProfileEditEmpInfoActivity : AppCompatActivity(), ICallBackEmpInfo {


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

           if(!TextUtils.isEmpty(profileData.userEmploymentInfo!!.employerType))
            {
                etTypeEmp.setText(profileData.userEmploymentInfo!!.employerType)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo!!.employerName))
            {
                etNameEmp.setText(profileData.userEmploymentInfo!!.employerName)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo!!.department))
            {
                etDepartment.setText(profileData.userEmploymentInfo!!.department)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo!!.position))
            {
                etPosition.setText(profileData.userEmploymentInfo!!.position)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo!!.employeesIdNumber))
            {
                etId.setText(profileData.userEmploymentInfo!!.employeesIdNumber)
            }
            if(!TextUtils.isEmpty(profileData.userEmploymentInfo!!.city))
            {
                etCity.setText(profileData.userEmploymentInfo!!.city)
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
            hashMapOLD["type"]          = ""+profileData.userEmploymentInfo!!.employerType
            hashMapOLD["name"]          = ""+profileData.userEmploymentInfo!!.employerName
            hashMapOLD["department"]    = ""+profileData.userEmploymentInfo!!.department
            hashMapOLD["position"]      = ""+profileData.userEmploymentInfo!!.position
            hashMapOLD["id"]            = ""+profileData.userEmploymentInfo!!.employeesIdNumber
            hashMapOLD["city"]          = ""+profileData.userEmploymentInfo!!.city

            val hashMapNew = HashMap<String, String>()
            hashMapNew["type"]          = etTypeEmp.text.toString()
            hashMapNew["name"]          = etNameEmp.text.toString()
            hashMapNew["department"]    = etDepartment.text.toString()
            hashMapNew["position"]      = etPosition.text.toString()
            hashMapNew["id"]            = etId.text.toString()
            hashMapNew["city"]          = etCity.text.toString()

            CommonMethod.hideKeyboardView(this@ProfileEditEmpInfoActivity)
            if(hashMapOLD == hashMapNew)
            {
                CommonMethod.customSnackBarError(llRoot,this@ProfileEditEmpInfoActivity,resources.getString(R.string.change_one_info))
            }
            else
            {
                if(TextUtils.isEmpty(etTypeEmp.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_type))
                }
                else if(TextUtils.isEmpty(etNameEmp.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_name))
                }
                else if(TextUtils.isEmpty(etDepartment.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_department))
                }
                else if(TextUtils.isEmpty(etPosition.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_occupation))
                }
                else if(TextUtils.isEmpty(etId.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_emp_id))
                }
                else if(TextUtils.isEmpty(etCity.text.toString().trim()))
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

        swipeRefreshLayout.isRefreshing=false
        val sharedPrefOBJ = SharedPref(this@ProfileEditEmpInfoActivity)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)
        onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onFailureEmpInfo(message: String) {

        buttonSubmit.isClickable=true
        swipeRefreshLayout.isRefreshing=false
    }

    override fun onSessionTimeOut(message: String) {

        swipeRefreshLayout.isRefreshing=false
        val dialogBuilder = AlertDialog.Builder(this@ProfileEditEmpInfoActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@ProfileEditEmpInfoActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@ProfileEditEmpInfoActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

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

                if(swipeRefreshLayout.isRefreshing && CommonMethod.isNetworkAvailable(this@ProfileEditEmpInfoActivity))
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditEmpInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    CommonMethod.hideKeyboardView(this@ProfileEditEmpInfoActivity)
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
        MyApplication.getInstance().trackScreenView(this@ProfileEditEmpInfoActivity::class.java.simpleName)

    }

}
