package com.app.l_pesa.profile.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.profile.adapter.IdListAdapter
import com.app.l_pesa.profile.inter.ICallBackBusinessInfo
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterBusinessInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_profile_edit_business_info.*
import kotlinx.android.synthetic.main.content_profile_edit_business_info.*
import java.util.*

class ProfileEditBusinessInfoActivity : AppCompatActivity(), ICallBackId, ICallBackBusinessInfo {

    private var idType=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_business_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditBusinessInfoActivity)

        val sharedPrefOBJ= SharedPref(this@ProfileEditBusinessInfoActivity)
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

        etIdType.isFocusable=false
        etIdType.setOnClickListener {

           showIdType()

        }

        if(!TextUtils.isEmpty(profileData.userBusinessInfo!!.businessName))
        {
            etBusinessName.setText(profileData.userBusinessInfo!!.businessName)
        }
        if(!TextUtils.isEmpty(profileData.userBusinessInfo!!.tinNumber))
        {
            etBusinessTinNo.setText(profileData.userBusinessInfo!!.tinNumber)
        }
        if(!TextUtils.isEmpty(profileData.userBusinessInfo!!.idType))
        {
            idType=profileData.userBusinessInfo!!.idType
            etIdType.setText(returnIdType(profileData.userBusinessInfo!!.idType))
        }
        else
        {
            idType="passport"
            etIdType.setText(resources.getString(R.string.passport))// Default
        }
        if(!TextUtils.isEmpty(profileData.userBusinessInfo!!.idNumber))
        {
            etBusinessIdNumber.setText(profileData.userBusinessInfo!!.idNumber)
        }

    }

    private fun returnIdType(idType:String): String {
        return when (idType) {
            "passport"          -> "Passport"
            "driving_license"   -> "Drivers License"
            "national_id"       -> "National ID"
            else                -> "Voters ID"
        }
    }

    override fun onClickIdType(position: Int, type: String) {

        val listId = arrayListOf("passport","driving_license","national_id","voter_card")
        idType=listId[position]
        etIdType.setText(type)
    }

    private fun showIdType()
    {
        val listTitle = arrayListOf("Passport", "Drivers License", "National ID","Voters ID")

        val dialog= Dialog(this@ProfileEditBusinessInfoActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = IdListAdapter(this@ProfileEditBusinessInfoActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditBusinessInfoActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()

    }

    private fun buttonClickEvent(profileData: ResUserInfo.Data)
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        buttonSubmit.setOnClickListener {


            val hashMapOLD = HashMap<String, String>()
            hashMapOLD["business_name"]     = ""+profileData.userBusinessInfo!!.businessName
            hashMapOLD["tin_number"]        = ""+profileData.userBusinessInfo!!.tinNumber
            hashMapOLD["id_type"]           = ""+profileData.userBusinessInfo!!.idType
            hashMapOLD["id_number"]         = ""+profileData.userBusinessInfo!!.idNumber

            val hashMapNew = HashMap<String, String>()
            hashMapNew["business_name"]     = etBusinessName.text.toString()
            hashMapNew["tin_number"]        = etBusinessTinNo.text.toString()
            hashMapNew["id_type"]           = idType
            hashMapNew["id_number"]         = etBusinessIdNumber.text.toString()

            CommonMethod.hideKeyboardView(this@ProfileEditBusinessInfoActivity)
            if(hashMapOLD==hashMapNew)
            {
                CommonMethod.customSnackBarError(llRoot,this@ProfileEditBusinessInfoActivity,resources.getString(R.string.change_one_info))
            }
            else
            {
                if(TextUtils.isEmpty(etBusinessName.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_business_name))
                }
                else if(TextUtils.isEmpty(etBusinessTinNo.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_tin_number))
                }
                else if(TextUtils.isEmpty(etBusinessIdNumber.text.toString().trim()))
                {
                    customSnackBarError(llRoot,resources.getString(R.string.required_id_number))
                }
                else
                {
                    if(CommonMethod.isNetworkAvailable(this@ProfileEditBusinessInfoActivity))
                    {
                        buttonSubmit.isClickable=false
                        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                        swipeRefreshLayout.isRefreshing=true

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("business_name",etBusinessName.text.toString())
                        jsonObject.addProperty("tin_number",etBusinessTinNo.text.toString())
                        jsonObject.addProperty("id_type",idType)
                        jsonObject.addProperty("id_number",etBusinessIdNumber.text.toString())

                        val presenterBusinessInfo= PresenterBusinessInfo()
                        presenterBusinessInfo.doChangeBusinessInfo(this@ProfileEditBusinessInfoActivity,jsonObject,this)

                    }
                    else
                    {
                        customSnackBarError(llRoot,resources.getString(R.string.no_internet))
                    }
                }
            }

        }
    }

    override fun onSuccessBusinessInfo() {

        swipeRefreshLayout.isRefreshing=false
        val sharedPrefOBJ = SharedPref(this@ProfileEditBusinessInfoActivity)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)
        onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onFailureBusinessInfo(message: String) {
        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        customSnackBarError(llRoot,message)
    }

    override fun onSessionTimeOut(message: String) {

        swipeRefreshLayout.isRefreshing=false
        val dialogBuilder = AlertDialog.Builder(this@ProfileEditBusinessInfoActivity)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@ProfileEditBusinessInfoActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@ProfileEditBusinessInfoActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

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


    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ProfileEditBusinessInfoActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this@ProfileEditBusinessInfoActivity).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message

        snackBarOBJ.show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

               if(swipeRefreshLayout.isRefreshing && CommonMethod.isNetworkAvailable(this@ProfileEditBusinessInfoActivity))
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditBusinessInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    CommonMethod.hideKeyboardView(this@ProfileEditBusinessInfoActivity)
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

    }

}
