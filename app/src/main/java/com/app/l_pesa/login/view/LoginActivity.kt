package com.app.l_pesa.login.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.SingleLineTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.calculator.view.LoanCalculatorActivity
import com.app.l_pesa.common.CommonEditTextRegular
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypefaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.login.inter.ICallBackLogin
import com.app.l_pesa.login.model.PinData
import com.app.l_pesa.login.presenter.PresenterLogin
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.otpview.view.OTPActivity
import com.app.l_pesa.pin.view.ForgotPinActivity
import com.app.l_pesa.pinview.view.PinSetActivity
import com.app.l_pesa.registration.view.RegistrationStepOneActivity
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set


class LoginActivity : AppCompatActivity(),ICallBackCountryList, ICallBackLogin {

    private lateinit var  progressDialog   : ProgressDialog
    private lateinit var  alCountry        : ArrayList<ResModelCountryList>
    private lateinit var  adapterCountry   : CountryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initLoader()
        loadCountry()
        loginProcess()
        forgotPin()
        doCalculateLoan()

      /*  AppUpdater(this@LoginActivity)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .setCancelable(true)
                .start()*/

    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@LoginActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun forgotPin()
    {
        txtForgotPin.makeLinks(resources.getString(R.string.forgot_pin),Pair(resources.getString(R.string.forgot_pin), View.OnClickListener {

            try {
                startActivity(Intent(this@LoginActivity, ForgotPinActivity::class.java))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            catch (exp: Exception)
            {}

        }))

    }

    private fun doCalculateLoan()
    {
        buttonLoanCalculator.setOnClickListener {

            buttonLoanCalculator.isClickable   = false
            val sharedPrefOBJ= SharedPref(this@LoginActivity)
            sharedPrefOBJ.currentLoanProduct=resources.getString(R.string.init)
            sharedPrefOBJ.businessLoanProduct=resources.getString(R.string.init)
            startActivity(Intent(this@LoginActivity, LoanCalculatorActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)

            Handler().postDelayed({
                buttonLoanCalculator.isClickable   = true
            }, 1000)

        }
    }

    @SuppressLint("MissingPermission")
    private fun loginProcess()
    {

        txtRegister.makeLinks(resources.getString(R.string.create_account),Pair("Create one!", View.OnClickListener {

            try {
                startActivity(Intent(this@LoginActivity, RegistrationStepOneActivity::class.java))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            catch (exp: Exception)
            {}

        }))

        etPhone.transformationMethod = SingleLineTransformationMethod.getInstance()
        etPhone.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }
        buttonLogin.setOnClickListener {

            verifyField()

        }

    }


    private fun AppCompatTextView.makeLinks(string:String,vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(string)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as AppCompatTextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = string.indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun hideKeyBoard()
    {
        try {
            CommonMethod.hideKeyboardView(this@LoginActivity)
        }
        catch (exp:Exception)
        {}

    }

    @SuppressLint("MissingPermission")
    private fun verifyField()
    {

        hideKeyBoard()
        if(etPhone.text.toString().length<8)
        {
            CommonMethod.customSnackBarError(rootLayout,this@LoginActivity,resources.getString(R.string.required_phone))
        }

        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                checkAndRequestPermissions()
            }
            else
            {
                doLoginProcess()
            }


        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun doLoginProcess()
    {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        var getIMEI = ""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId
        }

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (TextUtils.isEmpty(telephonyManager.simSerialNumber))
        {
            CommonMethod.customSnackBarError(rootLayout, this@LoginActivity, resources.getString(R.string.required_sim))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@LoginActivity))
            {

                val logger = AppEventsLogger.newLogger(this@LoginActivity)
                val params =  Bundle()
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Login")
                logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

                progressDialog.show()
                val sharedPrefOBJ= SharedPref(this@LoginActivity)
                buttonLogin.isClickable   = false

                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("country_code",sharedPrefOBJ.countryIsdCode)
                jsonObject.addProperty("platform_type","A")
                jsonObject.addProperty("device_token", FirebaseInstanceId.getInstance().token.toString())

                val jsonObjectRequestChild = JsonObject()
                jsonObjectRequestChild.addProperty("device_id", deviceId)
                jsonObjectRequestChild.addProperty("sdk",""+Build.VERSION.SDK_INT)
                jsonObjectRequestChild.addProperty("imei",getIMEI)
                jsonObjectRequestChild.addProperty("imsi","" + telephonyManager.subscriberId)
                jsonObjectRequestChild.addProperty("simSerial_no","" + telephonyManager.simSerialNumber)
                jsonObjectRequestChild.addProperty("sim_operator_Name","" + telephonyManager.simOperatorName)
                jsonObjectRequestChild.addProperty("screen_height",""+height)
                jsonObjectRequestChild.addProperty("screen_width",""+width)
                jsonObjectRequestChild.addProperty("device", Build.DEVICE)
                jsonObjectRequestChild.addProperty("model", Build.MODEL)
                jsonObjectRequestChild.addProperty("product", Build.PRODUCT)
                jsonObjectRequestChild.addProperty("manufacturer", Build.MANUFACTURER)
                jsonObjectRequestChild.addProperty("app_version", BuildConfig.VERSION_NAME)
                jsonObjectRequestChild.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

                jsonObject.add("device_data",jsonObjectRequestChild)

                println("JSON+"+jsonObject)

                val presenterLoginObj=PresenterLogin()
                presenterLoginObj.doLogin(this@LoginActivity,jsonObject,this)

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,this@LoginActivity,resources.getString(R.string.no_internet))
            }
        }


    }





    override fun onSuccessLogin(data: PinData) {

        Handler().postDelayed({
            buttonLogin.isClickable   = true
        }, 1000)

        dismiss()
        val sharedPrefOBJ=SharedPref(this@LoginActivity)

        if(data.next_step=="next_otp")
        {
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        else
        {
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

    }

    override fun onIncompleteLogin(message: String) {

        dismiss()
        Toast.makeText(this@LoginActivity,message,Toast.LENGTH_LONG).show()
        buttonLogin.isClickable   = true
        val intent = Intent(this@LoginActivity, RegistrationStepOneActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onErrorLogin(jsonMessage: String) {

        dismiss()
        buttonLogin.isClickable   = true
        CommonMethod.customSnackBarError(rootLayout,this@LoginActivity,jsonMessage)

    }

    override fun onFailureLogin(jsonMessage: String) {

        dismiss()
        buttonLogin.isClickable   = true
        CommonMethod.customSnackBarError(rootLayout,this@LoginActivity,jsonMessage)

    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

            etPhone.requestFocus()
            txtCountry.visibility=View.VISIBLE
            txtCountry.text = resModelCountryList.country_name
            etPhone.tag = resModelCountryList.country_code+"   "
            val sharedPrefOBJ= SharedPref(this@LoginActivity)
            sharedPrefOBJ.countryCode=resModelCountryList.code
            sharedPrefOBJ.countryName=resModelCountryList.country_name
            sharedPrefOBJ.countryIsdCode=resModelCountryList.country_code
            sharedPrefOBJ.countryFlag=resModelCountryList.image

    }


    private fun filterCountry(countryName: String) {

        val filteredCountry: ArrayList<ResModelCountryList> = ArrayList()

        for (country in alCountry) {
            if (country.country_name.toLowerCase().startsWith(countryName.toLowerCase())) {
                filteredCountry.add(country)
            }
            else
            {

            }
        }

        if(filteredCountry.size==0)
        {
            filteredCountry.clear()
            val emptyList=ResModelCountryList(0,resources.getString(R.string.search_result_not_found),"","","","","","","","","","","")
            filteredCountry.add(emptyList)
        }

        adapterCountry.filterList(filteredCountry)

    }

    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@LoginActivity)
        if(TextUtils.isEmpty(sharedPrefOBJ.countryIsdCode))
        {
            etPhone.tag="+000   "
            showCountry()
        }
        else
        {
            txtCountry.visibility=View.VISIBLE
            txtCountry.text = sharedPrefOBJ.countryName
            etPhone.tag=sharedPrefOBJ.countryIsdCode+"   "
        }

        txtCountry.setOnClickListener {

            showCountry()

        }

    }

    private fun showCountry()
    {
        val sharedPrefOBJ= SharedPref(this@LoginActivity)
        val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)

        val dialog= Dialog(this@LoginActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        alCountry= ArrayList()

        val recyclerView    = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        alCountry.addAll(countryData.countries_list)

        adapterCountry                  = CountryListAdapter(this@LoginActivity, alCountry,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@LoginActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = adapterCountry
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
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


    private fun checkAndRequestPermissions(): Boolean {

        val permissionPhoneState    = ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.READ_PHONE_STATE)

        val listPermissionsNeeded = ArrayList<String>()

        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_PERMISSIONS)
            return false
        }
        else
        {
            doLoginProcess()
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_ID_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_PHONE_STATE]         = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_PHONE_STATE]  == PackageManager.PERMISSION_GRANTED) {

                        doLoginProcess()
                        //else any one or both the permissions are not granted
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@LoginActivity, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Permissions are required for L-Pesa",
                                    DialogInterface.OnClickListener { _, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->

                                                finish()
                                        }
                                    })
                        } else {
                            permissionDialog("You need to give some mandatory permissions to continue. Do you want to go to app settings?")

                        }
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@LoginActivity,R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@LoginActivity,R.style.MyAlertDialogTheme)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }


    companion object {

        private const  val REQUEST_ID_PERMISSIONS = 1

    }


    override fun onBackPressed() {

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@LoginActivity::class.java.simpleName)

    }

}
