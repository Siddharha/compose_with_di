package com.app.l_pesa.registration.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.*
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.API.Result
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.*
import com.app.l_pesa.common.CommonMethod.openPrivacyUrl
import com.app.l_pesa.common.CommonMethod.openTermCondition
import com.app.l_pesa.common.CommonMethod.requestEmailHint
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.registration.inter.EmailVerifyListener
import com.app.l_pesa.registration.inter.ICallBackEmailVerify
import com.app.l_pesa.registration.inter.ICallBackRegisterOne
import com.app.l_pesa.registration.model.Data
import com.app.l_pesa.registration.model.EmailVerifyRequest
import com.app.l_pesa.registration.model.EmailVerifyResponse
import com.app.l_pesa.registration.model.RegistrationData
import com.app.l_pesa.registration.presenter.PresenterRegistrationOne
import com.app.l_pesa.registration.presenter.PresenterVerify
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.facebook.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_registration_step_one.toolbar
import kotlinx.android.synthetic.main.activity_registration_step_one.txtCountry
import kotlinx.android.synthetic.main.layout_registration_step_one.*
import kotlinx.android.synthetic.main.layout_registration_step_one.etPhone
import kotlinx.android.synthetic.main.layout_registration_step_one.rootLayout
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.messaging.FirebaseMessaging


class RegistrationStepOneActivity : AppCompatActivity(), ICallBackCountryList, ICallBackRegisterOne,
        GoogleApiClient.OnConnectionFailedListener, ICallBackEmailVerify, EmailVerifyListener {

    //working on google and fb email verification.
    private lateinit var progressDialog: ProgressDialog
    private lateinit var alCountry: ArrayList<ResModelCountryList>
    private lateinit var adapterCountry: CountryListAdapter
    private val EMAIL_HINT_REQ = 1
    private lateinit var btnGoogle: CustomButtonRegular

    //
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val SIGN_IN = 202
    private  val RC_SIGN_IN = 101
    private val callbackManager : CallbackManager by lazy{ CallbackManager.Factory.create()}
    //
    //private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_one)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepOneActivity)
       // FacebookSdk.sdkInitialize(applicationContext)

        btnGoogle = findViewById(R.id.btnGoogle)
        requestEmailHint(this,EMAIL_HINT_REQ)
        initData()
        val sharedPref = SharedPref(this@RegistrationStepOneActivity)

        tvTermsCons.richText(getString(R.string.privacy_term_condition)) {
            spannables = listOf(
                    41..61 to { openTermCondition(this@RegistrationStepOneActivity, sharedPref.countryCode) },
                    66..80 to { openPrivacyUrl(this@RegistrationStepOneActivity, sharedPref.countryCode) }
            )
        }

    }

    private fun initData() {
        initLoader()
        loadCountry()
        checkQualify()
        initFbLogin()
        setupNewGoogleSignIn()
        //
        //googleLogin()
        //fbLogin()
        onActionPerform()

    }

    private fun initFbLogin() {
        login_button.setReadPermissions("email")

    }

    private fun onActionPerform() {

        btnGoogle.setOnClickListener {
            signInGoogle()
        }

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if(isLoggedIn){

            btnFacebook.setOnClickListener {
                getEmailFromGraphAPI(accessToken)
                //fbLogOut()
            }

        }else{
            btnFacebook.setOnClickListener {
                login_button.performClick()
            }

        }

        login_button.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{  //For functionality. by default it will not be visible
            override fun onSuccess(result: LoginResult?) {
                getEmailFromGraphAPI(result?.accessToken!!)
            }

            override fun onCancel() {
                Log.e("res","Cancelled")
            }

            override fun onError(error: FacebookException?) {
                Log.e("res",error?.message!!)
            }
        })

    }

    private fun getEmailFromGraphAPI(accessToken: AccessToken) {
       // Log.e("token",accessToken.token)
        val parameters = Bundle()
        parameters.putString("fields", "email,id,name")

        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                accessToken.userId,
                parameters,
                HttpMethod.GET

        ) {
            val response = it.jsonObject

           // etEmail.setText(emailId)
            //btnSubmit.performClick()
            handleFacebookResult(response,accessToken.token)
            fbLogOut()
        }.executeAsync()
    }

    private fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

       /* btnGoogle.setOnClickListener {
            val intent = mGoogleSignInClient?.signInIntent
            val intent = Auth.GoogleSignInApi?.getSignInIntent(mGoogleApiClient)
            startActivityForResult(intent, SIGN_IN)

        }*/

    }

    private fun signInGoogle() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
       if(account !=null){
           Toast.makeText(this,"User Already signed in Logging out before New Login.",Toast.LENGTH_LONG).show()

           googleLogout()
       }else{
           val signInIntent = mGoogleSignInClient?.signInIntent
           startActivityForResult(signInIntent, RC_SIGN_IN)
       }
    }

    private fun setupNewGoogleSignIn() {
        val gso =  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun fbLogin() {
      //  callbackManager = CallbackManager.Factory.create()

      /*  btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    loadFbInformation(result?.accessToken)
                }

                override fun onCancel() {
                    // "Cancel".toast(this@RegistrationStepOneActivity)
                }

                override fun onError(error: FacebookException?) {
                    Log.e("response","${error?.localizedMessage}")
                      "Error + ${error?.localizedMessage}".toast(this@RegistrationStepOneActivity)
                }
            })
        }*/
    }

   /* private fun loadFbInformation(currentAccessToken: AccessToken?) {
        val graphRequest = GraphRequest.newMeRequest(currentAccessToken) { `object`, response ->
            try {
                val name = `object`.getString("first_name")
                val lastName = `object`.getString("last_name")
                val email = `object`.getString("email")
                val id = `object`.getString("id")
                //val imageUrl = "https://graph.facebook.com/$id/picture?type=normal"
                //val imageUrl = `object`.getString("picture")
                // "$email".toast(this@RegistrationStepOneActivity)
                val url = URL("https://graph.facebook.com/$id/picture?type=normal")
                // "$id / $name / $url".toast(this@RegistrationStepOneActivity)
                val intent = Intent(this@RegistrationStepOneActivity,VerifyMobileActivity::class.java)
                intent.putExtra("email",email)
                intent.putExtra("social_image",url)
                intent.putExtra("name",name)
                intent.putExtra("social", "Facebook")
                intent.putExtra("id",id)
                startActivity(intent)
            } catch (e: JSONException) {
                Log.i("error : ", e.localizedMessage!!)
            }
        }

        val bundle = Bundle()
        bundle.putString("fields", "first_name,last_name,email,id,picture.type(normal)")
        graphRequest.parameters = bundle
        graphRequest.executeAsync()
    }
*/
    private fun googleLogout() {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
            progressDialog.show()
        }
        mGoogleSignInClient?.signOut()?.addOnCompleteListener {
            if(progressDialog.isShowing){
                progressDialog.dismiss()
            }
            signInGoogle()
        }?.addOnFailureListener {
            if(progressDialog.isShowing){
                progressDialog.dismiss()
            }
        }

    }

    private fun fbLogOut() {
        LoginManager.getInstance().logOut()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            handleResult(result)
        }

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            progressDialog.show()
            mGoogleSignInClient?.signOut()?.addOnCompleteListener {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }
                handleSignInResult(task)
            }?.addOnFailureListener {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, "Unable to Login due to Unknown problem. please try again after some time!")
            }?.addOnCanceledListener {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, "Login Cancelled!")
            }

        }

        if(requestCode == EMAIL_HINT_REQ){
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                //Log.e("cred",credential?.id!!)

                etEmail.setText(credential?.id)
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        if(task?.isSuccessful!!){
            val result = task.result
            // "Hello : ${account?.email} : ${account?.photoUrl.toString()}".toast(this@RegistrationStepOneActivity)

            val intent = Intent(this@RegistrationStepOneActivity,VerifyMobileActivity::class.java)
            intent.putExtra("email",result?.email)
            intent.putExtra("social_image",result?.photoUrl.toString())
            intent.putExtra("name",result?.displayName)
            intent.putExtra("social", "Google")
            intent.putExtra("id",result?.id)
            startActivity(intent)
        }
    }

    private fun handleResult(result: GoogleSignInResult?) {
        if (result!!.isSuccess){
            val account = result.signInAccount
           // "Hello : ${account?.email} : ${account?.photoUrl.toString()}".toast(this@RegistrationStepOneActivity)

            val intent = Intent(this@RegistrationStepOneActivity,VerifyMobileActivity::class.java)
            intent.putExtra("email",account?.email)
            intent.putExtra("social_image",account?.photoUrl.toString())
            intent.putExtra("name",account?.displayName)
            intent.putExtra("social", "Google")
            intent.putExtra("id",account?.id)
            startActivity(intent)
        }
    }

    private fun handleFacebookResult(result: JSONObject?, token: String) {

        Log.e("resp",result.toString())
            // "Hello : ${account?.email} : ${account?.photoUrl.toString()}".toast(this@RegistrationStepOneActivity)
        val emailId = result?.optString("email")
        val userId = result?.optString("id")
        val userName = result?.optString("name")
        val socialImg = /*"https://graph.facebook.com/"+
                userId+
                "/picture?type=large&access_token=$token"*/
                "https://graph.facebook.com/"+userId+"/picture?type=large"

        Log.e("img",socialImg)
            val intent = Intent(this@RegistrationStepOneActivity,VerifyMobileActivity::class.java)
            intent.putExtra("email",emailId)
            intent.putExtra("social_image",socialImg)
            intent.putExtra("name",userName)
            intent.putExtra("social", "Facebook")
            intent.putExtra("id",userId)
            startActivity(intent)

    }

    private fun initLoader() {
        progressDialog = ProgressDialog(this@RegistrationStepOneActivity, R.style.MyAlertDialogStyle)
        val message = SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun checkQualify() {
        etEmail.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        btnSubmit.setOnClickListener {
            verifyField()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun verifyField() {
        hideKeyboard()

        if (TextUtils.isEmpty(etEmail.text.toString()) || !CommonMethod.isValidEmailAddress(etEmail.text.toString())) {
            CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, resources.getString(R.string.required_email))
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermissions()
            } else {
                //registrationProcess()
                emailVerify()
            }
        }
    }

    private fun emailVerify() {
        progressDialog.show()
        Log.i("email request : ",EmailVerifyRequest("Normal",etEmail.text.toString().trim()).toString())
        PresenterVerify().doEmailVerify(this@RegistrationStepOneActivity,
                EmailVerifyRequest(
                        "Normal",
                        etEmail.text.toString().trim()
                ), this)
    }

    override fun onEmailVerifyResponse(result: Result<Any>) {
        dismiss()
        when(result){
            is Result.Success -> {
                val s = (result.data as EmailVerifyResponse).status?.isSuccess.toString()

                "$s".toast(this@RegistrationStepOneActivity)

            }

            is Result.Error -> {
                (result.exception.localizedMessage.orEmpty()).toast(this@RegistrationStepOneActivity)
            }
        }
    }

    override fun onSuccessEmailVerify(data: Data) {
        dismiss()
        if (data.next == "otp_verify") {
            val intent = Intent(this@RegistrationStepOneActivity, EmailVerifyActivity::class.java)
            intent.putExtra("email",etEmail.text.toString().trim())
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }else if (data.next == "phone_number"){
            val intent = Intent(this@RegistrationStepOneActivity,VerifyMobileActivity::class.java)
            intent.putExtra("email",etEmail.text.toString().trim())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onErrorEmailVerify(jsonMessage: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, jsonMessage)

    }

    override fun onErrorEmailCode(code: JSONObject) {
        dismiss()
        //CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, code.getString("status"))
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun registrationProcess() {

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        var getIMEI = ""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId
        }

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (TextUtils.isEmpty(telephonyManager.simSerialNumber)) {
            CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, resources.getString(R.string.required_sim))
        } else {
            if (CommonMethod.isNetworkAvailable(this@RegistrationStepOneActivity)) {
                val sharedPref = SharedPref(this@RegistrationStepOneActivity)
                progressDialog.show()
                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no", etPhone.text.toString())
                jsonObject.addProperty("email_address", etEmail.text.toString())
                jsonObject.addProperty("country_code", sharedPref.countryIsdCode)
                jsonObject.addProperty("platform_type", "A")
                jsonObject.addProperty("device_token", FirebaseMessaging.getInstance().token.toString())

                val jsonObjectRequestChild = JsonObject()
                jsonObjectRequestChild.addProperty("device_id", deviceId)
                jsonObjectRequestChild.addProperty("sdk", "" + Build.VERSION.SDK_INT)
                jsonObjectRequestChild.addProperty("imei", getIMEI)
                jsonObjectRequestChild.addProperty("imsi", "" + telephonyManager.subscriberId)
                jsonObjectRequestChild.addProperty("simSerial_no", "" + telephonyManager.simSerialNumber)
                jsonObjectRequestChild.addProperty("sim_operator_Name", "" + telephonyManager.simOperatorName)
                jsonObjectRequestChild.addProperty("screen_height", "" + height)
                jsonObjectRequestChild.addProperty("screen_width", "" + width)
                jsonObjectRequestChild.addProperty("device", Build.DEVICE)
                jsonObjectRequestChild.addProperty("model", Build.MODEL)
                jsonObjectRequestChild.addProperty("product", Build.PRODUCT)
                jsonObjectRequestChild.addProperty("manufacturer", Build.MANUFACTURER)
                jsonObjectRequestChild.addProperty("app_version", BuildConfig.VERSION_NAME)
                jsonObjectRequestChild.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

                jsonObject.add("device_data", jsonObjectRequestChild)

                // println("JSON"+jsonObject.toString())

                val presenterRegistrationOneObj = PresenterRegistrationOne()
                presenterRegistrationOneObj.doRegistration(this@RegistrationStepOneActivity, jsonObject, this)

            } else {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, resources.getString(R.string.no_internet))
            }
        }


    }

    private fun hideKeyboard() {
        try {
            CommonMethod.hideKeyboardView(this@RegistrationStepOneActivity)
        } catch (exp: Exception) {
        }
    }


    private fun loadCountry() {
        val sharedPrefOBJ = SharedPref(this@RegistrationStepOneActivity)
        if (TextUtils.isEmpty(sharedPrefOBJ.countryIsdCode)) {
            etPhone.tag = "+000   "
            showCountry()
        } else {
            txtCountry.visibility = View.VISIBLE
            txtCountry.text = sharedPrefOBJ.countryName
            etPhone.tag = sharedPrefOBJ.countryIsdCode + "   "
        }

        txtCountry.setOnClickListener {

            showCountry()

        }

    }

    private fun showCountry() {
        val sharedPrefOBJ = SharedPref(this@RegistrationStepOneActivity)
        val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)

        val dialog = Dialog(this@RegistrationStepOneActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        alCountry = ArrayList()

        val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        alCountry.addAll(countryData.countries_list)

        adapterCountry = CountryListAdapter(this@RegistrationStepOneActivity, alCountry, dialog, this)
        recyclerView?.layoutManager = LinearLayoutManager(this@RegistrationStepOneActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = adapterCountry
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        etCountry!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                filterCountry(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

        etPhone.requestFocus()
        val sharedPrefOBJ = SharedPref(this@RegistrationStepOneActivity)
        sharedPrefOBJ.countryCode = resModelCountryList.code
        sharedPrefOBJ.countryName = resModelCountryList.country_name
        sharedPrefOBJ.countryIsdCode = resModelCountryList.country_code
        sharedPrefOBJ.countryFlag = resModelCountryList.image
        txtCountry.visibility = View.VISIBLE
        txtCountry.text = resModelCountryList.country_name
        etPhone.tag = sharedPrefOBJ.countryIsdCode + "   "

    }


    private fun filterCountry(countryName: String) {

        val filteredCountry: ArrayList<ResModelCountryList> = ArrayList()

        for (country in alCountry) {
            if (country.country_name.toLowerCase().startsWith(countryName.toLowerCase())) {
                filteredCountry.add(country)
            } else {
            }
        }

        if (filteredCountry.size == 0) {
            filteredCountry.clear()
            val emptyList = ResModelCountryList(0, resources.getString(R.string.search_result_not_found), "", "", "", "", "", "", "", "", "", "", "")
            filteredCountry.add(emptyList)
        }
        adapterCountry.filterList(filteredCountry)
    }


    override fun onSuccessRegistrationOne(data: RegistrationData) {
        dismiss()
        val sharedPref = SharedPref(this@RegistrationStepOneActivity)
        if (data.next == "step3") {
            sharedPref.accessToken = data.access_token
            val intent = Intent(this@RegistrationStepOneActivity, RegistrationStepFourActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        } else {
            sharedPref.accessToken = data.access_token
            sharedPref.verificationCode = data.otp
            val intent = Intent(this@RegistrationStepOneActivity, RegistrationStepTwoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onErrorRegistrationOne(jsonMessage: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepOneActivity, jsonMessage)
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionPhoneState = ContextCompat.checkSelfPermission(this@RegistrationStepOneActivity, Manifest.permission.READ_PHONE_STATE)
        val listPermissionsNeeded = ArrayList<String>()
        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_PERMISSIONS)
            return false
        } else {
            //registrationProcess()
            emailVerify()
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ID_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED) {
                        //registrationProcess()
                        emailVerify()
                        //else any one or both the permissions are not granted
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepOneActivity, Manifest.permission.READ_PHONE_STATE)) {
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
        AlertDialog.Builder(this@RegistrationStepOneActivity, R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@RegistrationStepOneActivity, R.style.MyAlertDialogTheme)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }

    companion object {

        private const val REQUEST_ID_PERMISSIONS = 1

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //googleLogout()
        //fbLogOut()
        val intent = Intent(this@RegistrationStepOneActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
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

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@RegistrationStepOneActivity::class.java.simpleName)

    }

    override fun onConnectionFailed(p0: ConnectionResult) {}



}
