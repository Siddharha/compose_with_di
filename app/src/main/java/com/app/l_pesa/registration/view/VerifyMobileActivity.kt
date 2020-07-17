package com.app.l_pesa.registration.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.provider.Settings
import android.text.*
import android.text.method.SingleLineTransformationMethod
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.*
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.registration.inter.MobileVerifyListener
import com.app.l_pesa.registration.model.DeviceData
import com.app.l_pesa.registration.model.NextStage
import com.app.l_pesa.registration.model.ReqVerifyMobile
import com.app.l_pesa.registration.presenter.PresenterVerify
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.sinch.verification.*
import kotlinx.android.synthetic.main.activity_verify_mobile.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VerifyMobileActivity : AppCompatActivity(), ICallBackCountryList, MobileVerifyListener {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var alCountry: ArrayList<ResModelCountryList>
    private lateinit var adapterCountry: CountryListAdapter

    private var email: String? = null
    private var image: String? = null
    private var tag: String? = null
    private var socialId: String? = null
    private var category: String? = null
    private var name: String? = null
    private var socId: String? = null

    private var uniqueID: String? = null

    companion object {
        private const val REQUEST_ID_PERMISSIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setContentView(R.layout.activity_verify_mobile)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        toolbar.title = "Registration Step One"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@VerifyMobileActivity)
        //
        email = intent.getStringExtra("email")
        image = intent.getStringExtra("social_image")
        tag = intent.getStringExtra("social")
        //
        socialId = intent.getStringExtra("id")
        socId = if (socialId != null){
            socialId!!
        }else{
            ""
        }
        println("id is now : $socId")
        println("id is : $socialId")
        name = intent.getStringExtra("name")
        category = when {
            tag.equals("Google") -> {
                "Google"
            }
            tag.equals("Facebook") -> {
                "FB"
            }
            else -> {
                "Normal"
            }
        }
        //
        initLoader()
        getMobile()
        loadCountry()
        onClickTermPolicy()
    }


    private fun initLoader() {
        progressDialog = ProgressDialog(this@VerifyMobileActivity, R.style.MyAlertDialogStyle)
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

    private fun onClickTermPolicy() {
        val sharedPref = SharedPref(this@VerifyMobileActivity)
        tvForgotTermPolicy.richText(getString(R.string.privacy_term_condition_login)) {
            spannables = listOf(
                    27..47 to { CommonMethod.openTermCondition(this@VerifyMobileActivity, sharedPref.countryCode) },
                    52..66 to { CommonMethod.openPrivacyUrl(this@VerifyMobileActivity, sharedPref.countryCode) }
            )
        }
    }

    private fun loadCountry() {
        val sharedPrefOBJ = SharedPref(this@VerifyMobileActivity)
        if (TextUtils.isEmpty(sharedPrefOBJ.countryIsdCode)) {
            etPhoneVerify.tag = "+000   "
            showCountry()
        } else {
            txtCountry.visibility = View.VISIBLE
            txtCountry.text = sharedPrefOBJ.countryName
            etPhoneVerify.tag = sharedPrefOBJ.countryIsdCode + "   "
        }

        txtCountry.setOnClickListener {
            showCountry()
        }
    }

    private fun showCountry() {
        val sharedPrefOBJ = SharedPref(this@VerifyMobileActivity)
        val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)

        val dialog = Dialog(this@VerifyMobileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        alCountry = ArrayList()

        val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        alCountry.addAll(countryData.countries_list)

        adapterCountry = CountryListAdapter(this@VerifyMobileActivity, alCountry, dialog, this)
        recyclerView?.layoutManager = LinearLayoutManager(this@VerifyMobileActivity, RecyclerView.VERTICAL, false)
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

    private fun getMobile() {
        etPhoneVerify.transformationMethod = SingleLineTransformationMethod.getInstance()
        etPhoneVerify.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }
        btnVerifyMobile.setOnClickListener {
            verifyField()
        }
    }

    @SuppressLint("MissingPermission")
    private fun verifyField() {
        hideKeyBoard()
        if (etPhoneVerify.text.toString().length < 9) {
            customSnackBarError(rootLayout, resources.getString(R.string.required_phone_email))
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //checkAndRequestPermissions()
                checkPermissions()
            } else {
                //doForgotPinProcess()
                //doMobileVerify()
                progressDialog.setTitle("Mobile Verification")
                progressDialog.setMessage("Please wait...")
                progressDialog.show()
                println("data : " + this.etPhoneVerify.tag + etPhoneVerify.text.toString())
                startVerification(etPhoneVerify.tag.toString() + etPhoneVerify.text.toString())
            }

        }
    }

    private fun checkPermissions(){
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                progressDialog.setTitle("Mobile Verification")
                progressDialog.setMessage("Verifieng Mobile No...")
                progressDialog.show()
                //"${etPhoneVerify.tag}${etPhoneVerify.text.toString()}".toast(this@VerifyMobileActivity)
                startVerification( etPhoneVerify.tag.toString() + etPhoneVerify.text.toString())
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@VerifyMobileActivity, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE
                ).check()
    }

    private fun startVerification(phone: String){
        val config = SinchVerification.config().applicationKey("f523cf73-5e20-4813-949f-f3cdca5d2244")
                .context(applicationContext).build()
        val listener = MyVerificationListener()
        val verification = SinchVerification.createFlashCallVerification(config,phone,listener)
        verification.initiate()
    }


    private fun doMobileVerify() {
        /*val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        var getIMEI = ""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId
        }
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)*/
        /*if (TextUtils.isEmpty(telephonyManager.simSerialNumber)) {
            CommonMethod.customSnackBarError(rootLayout, this@VerifyMobileActivity, resources.getString(R.string.required_sim))
        } else {*/
        if (CommonMethod.isNetworkAvailable(this@VerifyMobileActivity))
        {
            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels

            val sharedPrefOBJ = SharedPref(this@VerifyMobileActivity)

            progressDialog.setTitle("")
            progressDialog.setMessage("Loading....")
            progressDialog.show()
            if (sharedPrefOBJ.uuid.isEmpty()){
                uniqueID = UUID.randomUUID().toString()
                sharedPrefOBJ.uuid = uniqueID!!
                Log.i("VM : ","$uniqueID")
            }else{
                uniqueID = sharedPrefOBJ.uuid
                Log.i("VM 1 : ","$uniqueID")
            }

            /*val reqVerifyMobile = ReqVerifyMobile(
                    sharedPrefOBJ.countryIsdCode,
                    etPhoneVerify.text.toString().trim(),
                    email,
                    category,
                    socId,
                    FirebaseInstanceId.getInstance().token.toString(),
                    "A",
                    DeviceData(
                            uniqueID,
                            height.toString(),
                            width.toString(),
                            Build.DEVICE,
                            Build.MODEL,
                            Build.PRODUCT,
                            Build.MANUFACTURER,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE.toString()
                    )
            )*/

            val jsonObject = JsonObject()
            jsonObject.addProperty("country_code",sharedPrefOBJ.countryIsdCode)
            jsonObject.addProperty("phone_no",etPhoneVerify.text.toString().trim())
            jsonObject.addProperty("email_address",email)
            jsonObject.addProperty("category",category)
            jsonObject.addProperty("social_id",socId)
            jsonObject.addProperty("device_token",FirebaseInstanceId.getInstance().token.toString())
            jsonObject.addProperty("platform_type","A")

            val jsonObjectChild = JsonObject()
            jsonObjectChild.addProperty("device_id",uniqueID)
            jsonObjectChild.addProperty("screen_height",height.toString())
            jsonObjectChild.addProperty("screen_width",width.toString())
            jsonObjectChild.addProperty("device",Build.DEVICE)
            jsonObjectChild.addProperty("model",Build.MODEL)
            jsonObjectChild.addProperty("product",Build.PRODUCT)
            jsonObjectChild.addProperty("manufacturer",Build.MANUFACTURER)
            jsonObjectChild.addProperty("app_version",BuildConfig.VERSION_NAME)
            jsonObjectChild.addProperty("app_version_code",BuildConfig.VERSION_CODE.toString())

            jsonObject.add("device_data",jsonObjectChild)

            Log.i("verify request", jsonObject.toString())
            //Log.i("request : ", reqVerifyMobile.toString())
            PresenterVerify().doMobileVerify(this@VerifyMobileActivity, jsonObject, this)
        } else {
            CommonMethod.customSnackBarError(verifyLayout, this@VerifyMobileActivity, resources.getString(R.string.no_internet))
        }
    }

    override fun onResponseVerifyMobile(data: NextStage) {
        dismiss()
        val sharedPref = SharedPref(this@VerifyMobileActivity)
        if (data.next == "profile_image_name") {
            sharedPref.accessToken = data.access_token!!
            val intent = Intent(this@VerifyMobileActivity, RegistrationStepTwoActivity::class.java)
            intent.putExtra("social_image", image)
            intent.putExtra("name", name)
            startActivity(intent)
        }

    }

    override fun onFailure(msg: String) {
        dismiss()
        customSnackBarError(verifyLayout, msg)
    }


    override fun onClickCountry(resModelCountryList: ResModelCountryList) {
        etPhoneVerify.requestFocus()
        txtCountry.visibility = View.VISIBLE
        txtCountry.text = resModelCountryList.country_name
        etPhoneVerify.tag = resModelCountryList.country_code + ""
        val sharedPrefOBJ = SharedPref(this@VerifyMobileActivity)
        sharedPrefOBJ.countryCode = resModelCountryList.code
        sharedPrefOBJ.countryName = resModelCountryList.country_name
        sharedPrefOBJ.countryIsdCode = resModelCountryList.country_code
        sharedPrefOBJ.countryFlag = resModelCountryList.image

    }

    private fun checkAndRequestPermissions(): Boolean {

        val permissionPhoneState = ContextCompat.checkSelfPermission(this@VerifyMobileActivity, Manifest.permission.READ_PHONE_STATE)

        val listPermissionsNeeded = ArrayList<String>()

        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_PERMISSIONS)
            return false
        } else {
            //doForgotPinProcess()
            doMobileVerify()
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

                        //doForgotPinProcess()
                        doMobileVerify()
                        //else any one or both the permissions are not granted
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@VerifyMobileActivity, Manifest.permission.READ_PHONE_STATE)) {
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
        AlertDialog.Builder(this@VerifyMobileActivity, R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@VerifyMobileActivity, R.style.MyAlertDialogTheme)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }


    private fun customSnackBarError(view: View, message: String) {
        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@VerifyMobileActivity, R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle = customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message
        snackBarOBJ.show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@VerifyMobileActivity, RegistrationStepOneActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)

    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@VerifyMobileActivity::class.java.simpleName)

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
                CommonMethod.hideKeyboardView(this@VerifyMobileActivity)
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class MyVerificationListener: VerificationListener{
        override fun onInitiated(p0: InitiationResult?) {}
        override fun onInitiationFailed(e: Exception?) {
            dismiss()
            when (e) {
                is InvalidInputException -> {
                    Toast.makeText(
                            this@VerifyMobileActivity,
                            "Incorrect number provided",
                            Toast.LENGTH_LONG
                    ).show()
                }
                is ServiceErrorException -> {
                    Toast.makeText(this@VerifyMobileActivity, "Sinch service error" + e.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                }
                else -> {
                    Toast.makeText(
                            this@VerifyMobileActivity,
                            "Other system error, check your network state",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onVerified() {
           /* AlertDialog.Builder(this@VerifyMobileActivity)
                    .setMessage("Verification Successful!")
                    .setPositiveButton(
                            "Done"
                    ) { dialog, whichButton ->
                        dialog.cancel()
                    }
                    .show()*/
            doMobileVerify()
        }

        override fun onVerificationFailed(e: Exception?) {
            dismiss()
            when (e) {
                is CodeInterceptionException -> {
                    Toast.makeText(
                            this@VerifyMobileActivity,
                            "Intercepting the verification call automatically failed / " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG
                    ).show()
                }
                is ServiceErrorException -> {
                    Toast.makeText(this@VerifyMobileActivity, "Sinch service error", Toast.LENGTH_LONG)
                            .show()
                }
                else -> {
                    Toast.makeText(
                            this@VerifyMobileActivity,
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