package com.app.l_pesa.login.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.SingleLineTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
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
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.calculator.view.LoanCalculatorActivity
import com.app.l_pesa.common.*
import com.app.l_pesa.common.CommonMethod.openPrivacyUrl
import com.app.l_pesa.common.CommonMethod.openTermCondition
import com.app.l_pesa.common.CommonMethod.requestHint
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
import com.google.android.gms.auth.api.credentials.Credential
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.sinch.verification.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.rootLayout
import kotlinx.android.synthetic.main.activity_login.toolbar
import kotlinx.android.synthetic.main.activity_login.txtCountry
import kotlinx.android.synthetic.main.activity_verify_mobile.*
import java.io.IOException
import java.sql.DriverManager.println
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class LoginActivity : AppCompatActivity(),ICallBackCountryList, ICallBackLogin {

    private var deviceId: String? = null
    private lateinit var  progressDialog   : ProgressDialog
    private lateinit var  alCountry        : ArrayList<ResModelCountryList>
    private lateinit var  adapterCountry   : CountryListAdapter
    private val CREDENTIAL_PICKER_REQUEST = 1
    private val sharedPrefOBJ: SharedPref by lazy{SharedPref(this@LoginActivity)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val sharedPrefOBJ= SharedPref(this@LoginActivity)
        if(!TextUtils.isEmpty(sharedPrefOBJ.countryIsdCode)) {
            requestHint(this, CREDENTIAL_PICKER_REQUEST)
        }
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

        onClickTermPolicy()

    }

    private fun onClickTermPolicy(){
        val sharedPref = SharedPref(this@LoginActivity)
        tvLoginTermPolicy.richText(getString(R.string.privacy_term_condition_login)){
            spannables = listOf(
                    27..47 to { openTermCondition(this@LoginActivity, sharedPref.countryCode) },
                    52..66 to { openPrivacyUrl(this@LoginActivity, sharedPref.countryCode) }
            )
        }
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@LoginActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
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

        etPhone.setOnClickListener {
           // requestHint()
        }
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
                //checkAndRequestPermissions()
                doLoginProcess()
                //checkPermissions()
            }
            else
            {
                doLoginProcess()
            }


        }
    }

    private fun checkPermissions(){
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
               /* progressDialog.setTitle("Mobile Verification")
                progressDialog.setMessage("Verifieng Mobile No...")
                progressDialog.show()
                //"${etPhoneVerify.tag}${etPhoneVerify.text.toString()}".toast(this@VerifyMobileActivity)
                val sharedPref = SharedPref(this@LoginActivity)
                startVerification( sharedPref.countryIsdCode + etPhone.text.toString())*/
                doLoginProcess()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@LoginActivity, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        TedPermission.with(this@LoginActivity)
                .setPermissionListener(permissionListener)
                .setPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE
                ).check()
    }


    @SuppressLint("MissingPermission", "HardwareIds")
    private fun doLoginProcess()
    {
        //val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        /*var getIMEI = ""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId

        }*/
        val sharedPref = SharedPref(this@LoginActivity)
        if (sharedPref.uuid.isEmpty()){
            deviceId = UUID.randomUUID().toString()
            sharedPref.uuid = deviceId!!
            println("new uuid : $deviceId")
            Log.i("uuid 1 ", "$deviceId")
        }else{
            deviceId = sharedPref.uuid
           // deviceId = UUID.randomUUID().toString()
            //sharedPref.uuid = deviceId!!
            Log.i("uuid 2 ", "$deviceId")
           // Log.i("uuid 3 ", "${sharedPref.uuid}")
        }
        //11798829-ebf0-41e5-ba57-50e9a05226a1
        //val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

       /* if (TextUtils.isEmpty(telephonyManager.simSerialNumber)){
            CommonMethod.customSnackBarError(rootLayout, this@LoginActivity, resources.getString(R.string.required_sim))
        }
        else
        {}*/

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
            jsonObject.addProperty("is_new_version","new")
            jsonObject.addProperty("device_token", FirebaseMessaging.getInstance().token.toString())


            val jsonObjectRequestChild = JsonObject()
            jsonObjectRequestChild.addProperty("device_id", deviceId)

            /*jsonObjectRequestChild.addProperty("sdk",""+Build.VERSION.SDK_INT)
            jsonObjectRequestChild.addProperty("imei",getIMEI)
            jsonObjectRequestChild.addProperty("imsi","" + telephonyManager.subscriberId)
            jsonObjectRequestChild.addProperty("simSerial_no","" + telephonyManager.simSerialNumber)
            jsonObjectRequestChild.addProperty("sim_operator_Name","" + telephonyManager.simOperatorName)*/

            jsonObjectRequestChild.addProperty("screen_height",""+height)
            jsonObjectRequestChild.addProperty("screen_width",""+width)
            jsonObjectRequestChild.addProperty("device", Build.DEVICE)
            jsonObjectRequestChild.addProperty("model", Build.MODEL)
            jsonObjectRequestChild.addProperty("product", Build.PRODUCT)
            jsonObjectRequestChild.addProperty("manufacturer", Build.MANUFACTURER)
            jsonObjectRequestChild.addProperty("app_version", BuildConfig.VERSION_NAME)
            jsonObjectRequestChild.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

            jsonObject.add("device_data",jsonObjectRequestChild)

            println("Login Json Object $jsonObject")

            val presenterLoginObj=PresenterLogin()
            presenterLoginObj.doLogin(this@LoginActivity,jsonObject,this)

        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@LoginActivity,resources.getString(R.string.no_internet))
        }


    }





    override fun onSuccessLogin(data: PinData) {

        Handler().postDelayed({
            buttonLogin.isClickable   = true
        }, 1000)

        dismiss()
        val sharedPrefOBJ=SharedPref(this@LoginActivity)

        if (sharedPrefOBJ.uuid == data.master_device){
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }else{
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            progressDialog.setMessage("Verify User...")
            progressDialog.show()
            //println("number is " + sharedPrefOBJ.countryIsdCode+etPhone.text.toString())
            progressDialog.dismiss()
           /* Toast.makeText(this@LoginActivity,sharedPrefOBJ.countryIsdCode + etPhone.text.toString(), Toast.LENGTH_SHORT).show()
            startVerification(sharedPrefOBJ.countryIsdCode + etPhone.text.toString())*/
            //checkPermissions()
            MyApplication.getInstance().getGoogleAnalyticsLogger("Login",1) //0 for sign up
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)

        }

       /* if(data.next_step=="next_pin")a
        {
           *//* val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)*//*
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            progressDialog.setMessage("Loading...")
            progressDialog.show()
            startVerification(etPhone.tag.toString() + etPhone.text.toString())
        }
        else
        {
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@LoginActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }*/

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

    private fun startVerification(phone: String){
        val config = SinchVerification.config().applicationKey("f523cf73-5e20-4813-949f-f3cdca5d2244")
                .context(applicationContext).build()
        val listener = MyVerificationListener()
        //val defaultRegion = PhoneNumberUtils.getDefaultCountryIso(this@LoginActivity)
        //val phoneNumberInE164 = PhoneNumberUtils.formatNumberToE164(phone,defaultRegion)
        val verification = SinchVerification.createFlashCallVerification(config,phone,listener)
        verification.initiate()
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

        try{
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
        }catch (e:Exception){
            e.printStackTrace()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                //Log.e("cred",credential?.id!!)

                etPhone.setText(credential?.id?.removePrefix(sharedPrefOBJ.countryIsdCode))
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }

    inner class MyVerificationListener: VerificationListener {
        override fun onInitiated(p0: InitiationResult?) {}
        override fun onInitiationFailed(e: java.lang.Exception?) {
            dismiss()
            when (e) {
                is InvalidInputException -> {
                    Toast.makeText(
                            this@LoginActivity,
                            "Incorrect number provided",
                            Toast.LENGTH_LONG
                    ).show()
                }
                is ServiceErrorException -> {
                    Toast.makeText(this@LoginActivity, "Service Error Pls try again later" + e.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                }
                else -> {
                    Toast.makeText(
                            this@LoginActivity,
                            "Other system error, check your network state",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onVerified() {
            dismiss()
            val intent = Intent(this@LoginActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        override fun onVerificationFailed(e: java.lang.Exception?) {
            dismiss()
            when (e) {
                is CodeInterceptionException -> {
                    Toast.makeText(
                            this@LoginActivity,
                            "Intercepting the verification call automatically failed / " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG
                    ).show()
                }
                is ServiceErrorException -> {
                    Toast.makeText(this@LoginActivity, "Sinch service error", Toast.LENGTH_LONG)
                            .show()
                }
                else -> {
                    Toast.makeText(
                            this@LoginActivity,
                            "Other system error, check your network state",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onVerificationFallback() {

        }

    }


}

