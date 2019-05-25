package com.app.l_pesa.login.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.TelephonyManager
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonEditTextRegular
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.RunTimePermission
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.password.view.ForgotPasswordActivity
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.login.inter.ICallBackLogin
import com.app.l_pesa.login.presenter.PresenterLogin
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import com.app.l_pesa.login.model.PinData
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.otpview.view.OTPActivity
import com.app.l_pesa.pinview.view.PinSetActivity
import com.app.l_pesa.registration.view.RegistrationStepOneActivity
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson


class LoginActivity : AppCompatActivity(), ICallBackLogin, ICallBackCountryList, ICallBackDashboard {

    private var runTimePermission: RunTimePermission? = null
    private val permissionCode  = 200
    private var countryCode     ="+255"
    private var countryFound    = false

    private var listCountry                 : ArrayList<ResModelCountryList>? = null
    private lateinit var adapterCountry     : CountryListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        runTimePermission       =  RunTimePermission(this@LoginActivity)
        if (!runTimePermission!!.checkPermissionForPhoneState())
        {
            requestPermission()
        }

        loadCountry()
        loginProcess()
        forgetPassword()
    }

    private fun forgetPassword()
    {
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }


    private fun requestPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), permissionCode)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty()) {

                val requestPermissionForPhoneState     = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (requestPermissionForPhoneState ) {

                } else {


                    if (Build.VERSION.SDK_INT >= 23)
                    {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                            showMessageOKCancel("You need to allow this permissions",
                                    DialogInterface.OnClickListener { _, _ ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), permissionCode)
                                        }
                                    })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@LoginActivity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                .create()
                .show()
    }
    private fun loadCountry()
    {

       val sharedPrefOBJ=SharedPref(this@LoginActivity)
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
                    Glide.with(this@LoginActivity)
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
                Glide.with(this@LoginActivity)
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

    @SuppressLint("MissingPermission")
    private fun loginProcess()
    {
        txtRegister.makeLinks(Pair("Create one!", View.OnClickListener {

            try {
                startActivity(Intent(this@LoginActivity, RegistrationStepOneActivity::class.java))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            catch (exp: Exception)
            {}

        }))


        etPhone.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }
        txtLogin.setOnClickListener {

            verifyField()

        }


    }

    private fun AppCompatTextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(resources.getString(R.string.create_account))
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as AppCompatTextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = resources.getString(R.string.create_account).indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun verifyField()
    {
        CommonMethod.hideKeyboardView(this@LoginActivity)
        if(etPhone.text.toString().length<8)
        {
            CommonMethod.customSnackBarError(ll_root,this@LoginActivity,resources.getString(R.string.required_phone))
        }
        else
        {

            if(CommonMethod.isNetworkAvailable(this@LoginActivity))
            {
               progressBar.visibility = View.VISIBLE
               txtLogin.isClickable   = false

               if (ActivityCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((this@LoginActivity),
                                    Manifest.permission.CALL_PHONE)) {
                    } else {
                        ActivityCompat.requestPermissions((this@LoginActivity),
                                arrayOf(Manifest.permission.CALL_PHONE),
                                200)
                    }
                }

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

                val presenterLoginObj=PresenterLogin()
                presenterLoginObj.doLogin(this@LoginActivity,jsonObject,this)

            }
            else
            {
                CommonMethod.customSnackBarError(ll_root,this@LoginActivity,resources.getString(R.string.no_internet))
            }
        }
    }

    override fun onSuccessLogin(data: PinData) {

        progressBar.visibility=View.INVISIBLE
        txtLogin.isClickable   = false
        if(data.next_step=="next_otp")
        {
            val sharedPrefOBJ=SharedPref(this@LoginActivity)
            val json = Gson().toJson(data.post_data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        else
        {
            val sharedPrefOBJ=SharedPref(this@LoginActivity)
            val json = Gson().toJson(data.post_data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }


    }

    override fun onIncompleteLogin(message: String) {

        Toast.makeText(this@LoginActivity,message,Toast.LENGTH_LONG).show()
        progressBar.visibility = View.INVISIBLE
        txtLogin.isClickable   = true
        val intent = Intent(this@LoginActivity, RegistrationStepOneActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onErrorLogin(jsonMessage: String) {

      progressBar.visibility = View.INVISIBLE
      txtLogin.isClickable   = true
      CommonMethod.customSnackBarError(ll_root,this@LoginActivity,jsonMessage)

    }

    override fun onFailureLogin(jsonMessage: String) {

        progressBar.visibility = View.INVISIBLE
        txtLogin.isClickable   = true
        CommonMethod.customSnackBarError(ll_root,this@LoginActivity,jsonMessage)

    }

    override fun onSessionTimeOut(jsonMessage: String) {

    }

    private fun countrySpinner(countryList: ResModelData)
    {

        val dialog= Dialog(this@LoginActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        listCountry= ArrayList()
        val recyclerView    = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        listCountry!!.addAll(countryList.countries_list)
        adapterCountry                  = CountryListAdapter(this@LoginActivity, listCountry!!,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@LoginActivity, LinearLayoutManager.VERTICAL, false)
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

    override fun onSuccessDashboard(data: ResDashboard.Data) {

        val sharedPrefOBJ=SharedPref(this@LoginActivity)
        val gson                          = Gson()
        val dashBoardData                 = gson.toJson(data)
        sharedPrefOBJ.userDashBoard       = dashBoardData

        progressBar.visibility = View.INVISIBLE
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    override fun onFailureDashboard(jsonMessage: String) {

        val sharedPrefOBJ= SharedPref(this@LoginActivity)
        sharedPrefOBJ.removeShared()
        progressBar.visibility = View.INVISIBLE
        txtLogin.isClickable   = true
        CommonMethod.customSnackBarError(ll_root,this@LoginActivity,jsonMessage)
    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

        val sharedPref          =SharedPref(this@LoginActivity)
        sharedPref.countryCode  =resModelCountryList.code
        countryCode             =resModelCountryList.country_code
        val options = RequestOptions()
            options.centerCrop()
            Glide.with(this@LoginActivity)
            .load(resModelCountryList.image)
            .apply(options)
            .into(img_country)


    }

    override fun onBackPressed() {

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }


}
